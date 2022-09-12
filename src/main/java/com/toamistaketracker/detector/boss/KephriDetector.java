package com.toamistaketracker.detector.boss;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import com.toamistaketracker.detector.DelayedHitTilesTracker;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPCComposition;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.toamistaketracker.RaidRoom.KEPHRI;
import static com.toamistaketracker.ToaMistake.KEPHRI_BOMB;
import static com.toamistaketracker.ToaMistake.KEPHRI_SWARM_HEAL;

/**
 * The Kephri bomb, exploding scarabs, and exploding eggs all reuse the same set of graphics IDs on impact, though they
 * each behave differently. The Kephri bomb spawns a graphics object on *each* of its tiles (including the 3x3) on
 * impact, but the exploding scarab and the exploding egg only spawn it on the 1x1 hit tile. The exploding scarab still
 * deals damage in a 3x3 area if the Aerial Assault invocation is turned on. Therefore, using just GraphicsObjectCreated
 * events on the explosion won't suffice, so instead we use the shadows that spawn from the incoming bomb attacks.
 */
@Slf4j
@Singleton
public class KephriDetector extends BaseMistakeDetector {

    private static final int KEPHRI_BOMB_SHADOW_GRAPHICS_ID = 1447; // 3 ticks is 1446
    private static final int KEPHRI_BOMB_SHADOW_EXPLOSION_DELAY_IN_TICKS = 4;
    private static final Map<Integer, Integer> KEPHRI_BOMB_SHADOW_GRAPHICS = Map.of( // Graphics ID -> tick delay
            1447, 4,
            1446, 3,
            2111, 2
    ); // TODO: These need to be wiped on *any* phase transition

    private static final Set<Integer> KEPHRI_BOMB_GRAPHICS_ID = Set.of(2156, 2157, 2158, 2159);
    private static final int SWARM_HEAL_ANIMATION_ID = 9607;
    private static final String SWARM_NAME = "Scarab Swarm";
    private static final int KEPHRI_BOMB_PROJECTILE_ID = 2266;
    private static final int EXPLODING_SCARAB_PROJECTILE_ID = 2147;

    private static final String KEPHRI_NAME = "Kephri";
    private static final Set<Integer> KEPHRI_PHASE_IDS = Set.of(11719, 11720, 11721);
    private static final int KEPHRI_DEAD_ID = 11722;

    private int swarmsHealing;

    @Getter
    private final DelayedHitTilesTracker bombHitTiles = new DelayedHitTilesTracker();

    public KephriDetector() {
        swarmsHealing = 0;
    }

    @Override
    public void cleanup() {
        swarmsHealing = 0;
        bombHitTiles.clear();
    }

    @Override
    public RaidRoom getRaidRoom() {
        return KEPHRI;
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        List<ToaMistake> mistakes = new ArrayList<>();

        for (int i = 0; i < swarmsHealing; i++) {
            mistakes.add(KEPHRI_SWARM_HEAL);
        }

        if (bombHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(KEPHRI_BOMB);
        }

        return mistakes;
    }

    @Override
    public void afterDetect() {
        swarmsHealing = 0;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        bombHitTiles.activateHitTilesForTick(client.getTickCount());
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        int id = event.getGraphicsObject().getId();
        if (KEPHRI_BOMB_SHADOW_GRAPHICS.containsKey(id)) {
            int activationTick = client.getTickCount() + KEPHRI_BOMB_SHADOW_GRAPHICS.get(id);
            bombHitTiles.addHitTile(activationTick, getWorldPoint(event.getGraphicsObject()));
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() == null || event.getActor().getName() == null) return;

        String name = Text.removeTags(event.getActor().getName());
        if (SWARM_NAME.equals(name) &&
                event.getActor().getAnimation() == SWARM_HEAL_ANIMATION_ID &&
                !event.getActor().isDead()) {
            swarmsHealing += 1;
        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        if (!KEPHRI_NAME.equals(event.getNpc().getName())) return;

        if (isPhaseTransition(event.getOld(), event.getNpc().getComposition())) {
            log.debug("Found Kephri transition -- clearing bomb hit tiles");
            bombHitTiles.clear();
        } else if (event.getNpc().getId() == KEPHRI_DEAD_ID) {
            shutdown(); // Shut down and clean up all state. Any incoming bombs shouldn't count as mistakes.
        }
    }

    private boolean isPhaseTransition(NPCComposition oldComp, NPCComposition newComp) {
        return KEPHRI_PHASE_IDS.contains(oldComp.getId()) &&
                KEPHRI_PHASE_IDS.contains(newComp.getId()) &&
                oldComp.getId() != newComp.getId();
    }
}