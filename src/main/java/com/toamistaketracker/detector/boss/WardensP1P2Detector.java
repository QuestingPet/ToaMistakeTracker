package com.toamistaketracker.detector.boss;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import com.toamistaketracker.detector.tracker.DelayedHitTilesTracker;
import com.toamistaketracker.detector.tracker.InstantHitTilesTracker;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Animation;
import net.runelite.api.DynamicObject;
import net.runelite.api.GameObject;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.toamistaketracker.RaidRoom.WARDENS_P1_P2;
import static com.toamistaketracker.ToaMistake.WARDENS_P1_PYRAMID;
import static com.toamistaketracker.ToaMistake.WARDENS_P2_BIND;
import static com.toamistaketracker.ToaMistake.WARDENS_P2_BOMBS;
import static com.toamistaketracker.ToaMistake.WARDENS_P2_DDR;
import static com.toamistaketracker.ToaMistake.WARDENS_P2_SPECIAL_PRAYER;
import static com.toamistaketracker.ToaMistake.WARDENS_P2_WINDMILL;

/**
 * The pyramids are GameObjects that change their animations through changing their Renderables. Since we can't
 * listen for an AnimationChanged event for GameObjects, we manually detect when the animation is the one that hurts
 * the player. Also, the pyramid animation starts 1 tick earlier and ends 1 tick earlier than when the player actually
 * takes damage, so we delay the detection by a tick.
 *
 * DDR tiles spawn all of their graphics objects at once and delay their activation for some set number of ticks. We
 * calculate that and delay the hit tile activations with the new {@link DelayedHitTilesTracker}. This means we only
 * need to check for the player standing on the hit tile once the activation tick is played.
 *
 * The Bombs are the same, as the outer ring activates one tile later than the inner ones, using graphics objects to
 * detect
 *
 * The windmill attack is similar, but activates instantly on the current game tick. For that, we use the aptly named
 * {@link InstantHitTilesTracker} which is also a new class that helps track these hit tiles and when they should
 * activate. Once again graphics objects are used for detection here.
 *
 * Sometimes the obelisk can be in different phases but use an attack from a separate special. For example, the DDR
 * phase starts with graphics ids that are the same as the windmill, but we should count that as screwing up the DDR
 * phase not the windmill phase, and as such we detect the obelisk phase to account for that.
 */
@Slf4j
@Singleton
public class WardensP1P2Detector extends BaseMistakeDetector {

    // P1 constants
    private static final int RED_PYRAMID_GAME_OBJECT_ID = 45750;
    private static final int YELLOW_PYRAMID_GAME_OBJECT_ID = 45751;
    private static final int PYRAMID_ACTIVE_ANIMATION_ID = 9524;
    private final int PYRAMID_HIT_DELAY_IN_TICKS = 1;
    private static final int DEATH_DOT_PROJECTILE_ID = 2237;
    private static final int DISPERSE_PROJECTILE_ID = 2238;

    // P2 constants
    private static final String OBELISK_NAME = "Obelisk";
    private static final int DDR_GRAPHICS_ID = 2235;
    private static final int WINDMILL_SHADOW_GRAPHICS_ID = 2236;
    private static final int WINDMILL_HIT_GRAPHICS_ID = 2234;
    private static final int BOMB_GRAPHICS_ID = 2198;
    private static final int DDR_HIT_DELAY_IN_TICKS = 1;
    private static final int LIGHTNING_HIT_DELAY_IN_TICKS = 0;
    private static final int OBELISK_DDR_ANIMATION_ID = 9732;
    private static final int OBELISK_WINDMILL_ANIMATION_ID = 9733;
    private static final int OBELISK_BOMB_ANIMATION_ID = 9727;
    private static final int OBELISK_DEATH_ANIMATION_ID = 9734;
    private static final int OBELISK_DDR_LIGHTNING_GRAPHICS_ID = 2199;
    private static final int OBELISK_WINDMILL_LIGHTNING_GRAPHICS_ID = 2200;
    private static final int PLAYER_BIND_ANIMATION_ID = 9714;
    // Projectile ID -> correct overhead icon
    private final Map<Integer, HeadIcon> SPECIAL_PRAYER_ATTACKS = Map.of(
            2204, HeadIcon.MELEE,
            2206, HeadIcon.RANGED,
            2208, HeadIcon.MAGIC
    );

    @RequiredArgsConstructor
    enum ObeliskPhase {
        DDR(OBELISK_DDR_ANIMATION_ID),
        WINDMILL(OBELISK_WINDMILL_ANIMATION_ID),
        BOMBS(OBELISK_BOMB_ANIMATION_ID),
        DEATH(OBELISK_DEATH_ANIMATION_ID);

        @NonNull
        @Getter
        private final Integer animationId;

        static ObeliskPhase fromAnimationId(int animationId) {
            for (ObeliskPhase obeliskPhase : ObeliskPhase.values()) {
                if (obeliskPhase.getAnimationId() == animationId) {
                    return obeliskPhase;
                }
            }

            return null;
        }
    }

    // P1 fields
    @Getter
    private final List<GameObject> activePyramids = new ArrayList<>();
    @Getter
    private final DelayedHitTilesTracker pyramidHitTiles = new DelayedHitTilesTracker();

    // P2 fields
    private ObeliskPhase obeliskPhase;
    @Getter
    private final DelayedHitTilesTracker ddrHitTiles = new DelayedHitTilesTracker();
    @Getter
    private final InstantHitTilesTracker windmillHitTiles = new InstantHitTilesTracker();
    @Getter
    private final DelayedHitTilesTracker bombHitTiles = new DelayedHitTilesTracker();
    private final Set<String> raidersBound = new HashSet<>();

    @Getter
    // This is really just used for timing, not for the tile itself
    private final DelayedHitTilesTracker specialPrayerHitTiles = new DelayedHitTilesTracker();
    private Integer currentSpecialPrayerProjectileId;

    @Override
    public void cleanup() {
        activePyramids.clear();
        pyramidHitTiles.clear();

        obeliskPhase = null;
        ddrHitTiles.clear();
        windmillHitTiles.clear();
        bombHitTiles.clear();
        raidersBound.clear();
        specialPrayerHitTiles.clear();
        currentSpecialPrayerProjectileId = null;
    }

    @Override
    public RaidRoom getRaidRoom() {
        return WARDENS_P1_P2;
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        List<ToaMistake> mistakes = new ArrayList<>();

        if (pyramidHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P1_PYRAMID);
        }

        if (ddrHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P2_DDR);
        }

        if (windmillHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P2_WINDMILL);
        }

        if (bombHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P2_BOMBS);
        }

        if (raidersBound.contains(raider.getName())) {
            mistakes.add(WARDENS_P2_BIND);
        }

        if (isSpecialPrayerHit(raider)) {
            mistakes.add(WARDENS_P2_SPECIAL_PRAYER);
        }

        return mistakes;
    }

    @Override
    public void afterDetect() {
        raidersBound.clear();
        if (!specialPrayerHitTiles.getActiveHitTiles().isEmpty()) {
            currentSpecialPrayerProjectileId = null;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() == null || event.getActor().getName() == null) return;

        String name = Text.removeTags(event.getActor().getName());
        if (event.getActor() instanceof NPC && OBELISK_NAME.equals(name)) {
            ObeliskPhase newObeliskPhase = ObeliskPhase.fromAnimationId(event.getActor().getAnimation());
            if (newObeliskPhase != null) {
                log.debug("Found new obelisk phase: {}", newObeliskPhase.name());
                obeliskPhase = newObeliskPhase;
            }
        } else if (event.getActor() instanceof Player && raidState.getRaiders().containsKey(name)) {
            if (event.getActor().getAnimation() == PLAYER_BIND_ANIMATION_ID) {
                raidersBound.add(name);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        computePyramidHitTiles();
        pyramidHitTiles.activateHitTilesForTick(client.getTickCount());

        ddrHitTiles.activateHitTilesForTick(client.getTickCount());
        windmillHitTiles.activateHitTilesForTick(client.getTickCount());
        bombHitTiles.activateHitTilesForTick(client.getTickCount());
        specialPrayerHitTiles.activateHitTilesForTick(client.getTickCount());
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
        int activationTick;
        switch (event.getGraphicsObject().getId()) {
            case DDR_GRAPHICS_ID:
                activationTick = getActivationTick(event.getGraphicsObject(), DDR_HIT_DELAY_IN_TICKS);
                ddrHitTiles.addHitTile(activationTick, getWorldPoint(event.getGraphicsObject()));
                break;
            case OBELISK_DDR_LIGHTNING_GRAPHICS_ID:
                activationTick = getActivationTick(event.getGraphicsObject(), LIGHTNING_HIT_DELAY_IN_TICKS);
                ddrHitTiles.addHitTile(activationTick, getWorldPoint(event.getGraphicsObject()));
                break;
            case WINDMILL_HIT_GRAPHICS_ID:
                if (obeliskPhase == ObeliskPhase.DDR) {
                    ddrHitTiles.addHitTile(client.getTickCount(), getWorldPoint(event.getGraphicsObject()));
                } else if (obeliskPhase == ObeliskPhase.WINDMILL) {
                    windmillHitTiles.addHitTile(getWorldPoint(event.getGraphicsObject()));
                }
                break;
            case OBELISK_WINDMILL_LIGHTNING_GRAPHICS_ID:
                windmillHitTiles.addHitTile(getWorldPoint(event.getGraphicsObject()));
                break;
            case BOMB_GRAPHICS_ID:
                if (obeliskPhase == ObeliskPhase.BOMBS) {
                    activationTick = getActivationTick(event.getGraphicsObject(), LIGHTNING_HIT_DELAY_IN_TICKS);
                    bombHitTiles.addHitTile(activationTick, getWorldPoint(event.getGraphicsObject()));
                }
                break;
        }
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        if (SPECIAL_PRAYER_ATTACKS.containsKey(event.getProjectile().getId())) {
            if (currentSpecialPrayerProjectileId != null) return;
            log.debug("{} - No others out yet", client.getTickCount());

            if (event.getProjectile().getRemainingCycles() <= CYCLES_PER_GAME_TICK) {
                // There's no way there's a new projectile that only has at most 1 game tick left. It's probably
                // hanging around from the previous attack, so let's ignore
                log.debug("{} - Found special prayer projectile with too few remaining cycles. Ignoring.",
                        client.getTickCount());
                return;
            }

            int activationTick = getActivationTick(event.getProjectile());

            // Add dummy location for the correct activation tick
            specialPrayerHitTiles.addHitTile(activationTick, WorldPoint.fromLocal(client, event.getPosition()));
            currentSpecialPrayerProjectileId = event.getProjectile().getId();
            log.debug("{} - Added special prayer projectile {} for tick: {}", client.getTickCount(),
                    event.getProjectile().getId(), activationTick);
        }
    }

    private boolean isSpecialPrayerHit(Raider raider) {
        if (currentSpecialPrayerProjectileId == null) {
            return false;
        }

        if (specialPrayerHitTiles.getActiveHitTiles().isEmpty()) {
            return false;
        }

        if (vengeanceTracker.didPopVengeance(raider)) {
            return false;
        }

        // We know there's a hit this tick. Check the prayers
        log.debug("{} - {} was praying {} when special prayer procc'd", client.getTickCount(), raider.getName(),
                raider.getPlayer().getOverheadIcon() == null ? "NONE" : raider.getPlayer().getOverheadIcon().name());

        HeadIcon playerHeadIcon = raider.getPlayer().getOverheadIcon();
        if (playerHeadIcon == null) {
            return true;
        }

        HeadIcon requiredHeadIcon = SPECIAL_PRAYER_ATTACKS.get(currentSpecialPrayerProjectileId);
        if (requiredHeadIcon == null) {
            // Can't happen, but just in case, no mistake
            return false;
        }

        return playerHeadIcon != requiredHeadIcon;
    }

    private void computePyramidHitTiles() {
        activePyramids.forEach(pyramid -> {
            if (pyramid.getRenderable() instanceof DynamicObject) {
                Animation animation = ((DynamicObject) pyramid.getRenderable()).getAnimation();
                if (animation != null && animation.getId() == PYRAMID_ACTIVE_ANIMATION_ID) {
                    int activationTick = client.getTickCount() + PYRAMID_HIT_DELAY_IN_TICKS;
                    pyramidHitTiles.addHitTiles(activationTick, compute3By3TilesFromCenter(pyramid.getWorldLocation()));
                }
            }
        });
    }

    private boolean isPyramid(GameObject gameObject) {
        return gameObject.getId() == RED_PYRAMID_GAME_OBJECT_ID || gameObject.getId() == YELLOW_PYRAMID_GAME_OBJECT_ID;
    }
}