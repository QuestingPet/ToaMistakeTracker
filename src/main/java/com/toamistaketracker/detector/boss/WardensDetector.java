package com.toamistaketracker.detector.boss;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import com.toamistaketracker.detector.DelayedHitTilesTracker;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Animation;
import net.runelite.api.Constants;
import net.runelite.api.DynamicObject;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.toamistaketracker.RaidRoom.WARDENS;
import static com.toamistaketracker.ToaMistake.WARDENS_P1_PYRAMID;
import static com.toamistaketracker.ToaMistake.WARDENS_P2_BOMBS;
import static com.toamistaketracker.ToaMistake.WARDENS_P2_DDR;
import static com.toamistaketracker.ToaMistake.WARDENS_P2_WINDMILL;

/**
 * The pyramids are GameObjects that change their animations through changing their Renderables. Since we can't
 * listen for an AnimationChanged event for GameObjects, we manually detect when the animation is the one that hurts
 * the player. Also, the pyramid animation starts 1 tick earlier and ends 1 tick earlier than when the player actually
 * takes damage, so we delay the detection by a tick.
 */
@Slf4j
@Singleton
public class WardensDetector extends BaseMistakeDetector {

    // TODO: Split this up into separate classes per phase for readability

    // P1 constants
    private static final int RED_PYRAMID_GAME_OBJECT_ID = 45750;
    private static final int YELLOW_PYRAMID_GAME_OBJECT_ID = 45751;
    private static final int PYRAMID_ACTIVE_ANIMATION_ID = 9524;
    private static final int DEATH_DOT_PROJECTILE_ID = 2237;
    private static final int DISPERSE_PROJECTILE_ID = 2238;

    // P2 constants
    private static final int DDR_GRAPHICS_ID = 2235;
    private static final int WINDMILL_SHADOW_GRAPHICS_ID = 2236;
    private static final int WINDMILL_HIT_GRAPHICS_ID = 2234;
    private static final int BOMB_GRAPHICS_ID = 2198;

    // P1 fields
    @Getter
    private final List<GameObject> activePyramids = new ArrayList<>();
    @Getter
    private final Set<WorldPoint> pyramidHitTiles = new HashSet<>();
    private final Set<WorldPoint> pyramidHitTilesForNextTick = new HashSet<>();

    // P2 fields
    @Getter
    private final DelayedHitTilesTracker ddrHitTiles = new DelayedHitTilesTracker();
    @Getter
    private final DelayedHitTilesTracker bombHitTiles = new DelayedHitTilesTracker();
    private final Set<WorldPoint> windmillHitTiles = new HashSet<>();

    @Override
    public void cleanup() {
        activePyramids.clear();
        pyramidHitTiles.clear();
        pyramidHitTilesForNextTick.clear();

        ddrHitTiles.clear();
        bombHitTiles.clear();
        windmillHitTiles.clear();
    }

    @Override
    public RaidRoom getRaidRoom() {
        return WARDENS;
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        raider.getPlayer().setOverheadText("" + client.getTickCount());

        List<ToaMistake> mistakes = new ArrayList<>();

        if (pyramidHitTiles.contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P1_PYRAMID);
        }

        if (ddrHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P2_DDR);
        }

        if (bombHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P2_BOMBS);
        }

        if (windmillHitTiles.contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P2_WINDMILL);
        }

        return mistakes;
    }

    @Override
    public void afterDetect() {
        pyramidHitTiles.clear();
        pyramidHitTiles.addAll(pyramidHitTilesForNextTick);
        pyramidHitTilesForNextTick.clear();

//        bombHitTiles.clear();
        windmillHitTiles.clear();
        ;
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        activePyramids.forEach(pyramid -> {
            if (pyramid.getRenderable() instanceof DynamicObject) {
                Animation animation = ((DynamicObject) pyramid.getRenderable()).getAnimation();
                if (animation != null && animation.getId() == PYRAMID_ACTIVE_ANIMATION_ID) {
                    pyramidHitTilesForNextTick.addAll(compute3By3TilesFromCenter(pyramid.getWorldLocation()));
                }
            }
        });

        ddrHitTiles.activateHitTilesForTick(client.getTickCount());
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (isPyramid(event.getGameObject())) {
            activePyramids.add(event.getGameObject());
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        if (isPyramid(event.getGameObject())) {
            activePyramids.remove(event.getGameObject());
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        final int activationTick;
        switch (event.getGraphicsObject().getId()) {
            case DDR_GRAPHICS_ID:
                activationTick = getActivationTick(event.getGraphicsObject(), 1);
                ddrHitTiles.addHitTile(activationTick, getWorldPoint(event.getGraphicsObject()));
                break;
            case WINDMILL_SHADOW_GRAPHICS_ID:
                break;
            case WINDMILL_HIT_GRAPHICS_ID:
                windmillHitTiles.add(getWorldPoint(event.getGraphicsObject()));
                break;
            case BOMB_GRAPHICS_ID:
                // TODO: Maybe find the one that has the same startCycle as now, and then calculate the rest of the tiles manually. outer ring is 1 tick later.
                activationTick = getActivationTick(event.getGraphicsObject(), 0);
                log.debug("{} - bomb spawned: {} | {}, {}", client.getTickCount(), client.getGameCycle(),
                        event.getGraphicsObject().getStartCycle(), getWorldPoint(event.getGraphicsObject()));
                bombHitTiles.addHitTile(activationTick, getWorldPoint(event.getGraphicsObject()));
                break;
        }
    }

    private boolean isPyramid(GameObject gameObject) {
        return gameObject.getId() == RED_PYRAMID_GAME_OBJECT_ID || gameObject.getId() == YELLOW_PYRAMID_GAME_OBJECT_ID;
    }
}