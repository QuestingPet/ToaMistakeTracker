package com.toamistaketracker.detector.boss;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Constants;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.toamistaketracker.RaidRoom.KEPHRI;
import static com.toamistaketracker.ToaMistake.KEPHRI_BOMB;
import static com.toamistaketracker.ToaMistake.KEPHRI_SWARM_HEAL;

/**
 * Eggs reuse the same graphics IDs as the kephri bombs, so if you're standing under one as it hatches, it will count
 * as a bomb mistake. Additionally, it still damages as long as you're in a 3x3 around the egg, but only the tile
 * under the egg hatch spawns the graphics ID. While it's relatively trivial to account for this, I'm going to just
 * leave this behavior as-is. This means: if you are under an egg as it hatches you will get a bomb mistake. If you are
 * adjacent to an egg while it hatches (and thus still receive damage), you will not get any mistake.
 *
 * The Kephri bomb, exploding scarabs, and exploding eggs all reuse the same set of graphics IDs on impact, though they
 * each behave differently. The Kephri bomb spawns a graphics object on *each* of its tiles (including the 3x3) on
 * impact, but the exploding scarab and the exploding egg only spawn it on the 1x1 hit tile. The exploding scarab still
 * deals damage in a 3x3 area if the Aerial Assault invocation is turned on. Therefore, using just GraphicsObjectCreated
 * events won't suffice, and instead we look towards using ProjectileMoved for the two Kephri
 * bomb attacks.
 */
@Slf4j
@Singleton
public class KephriDetector extends BaseMistakeDetector {

    private static final int CYCLES_PER_GAME_TICK = Constants.GAME_TICK_LENGTH / Constants.CLIENT_TICK_LENGTH;

    private static final int KEPHRI_BOMB_SHADOW_GRAPHICS_ID = 1447; // 3 ticks is 1446
    private static final int KEPHRI_BOMB_SHADOW_EXPLOSION_DELAY_IN_TICKS = 4;
    private static final Map<Integer, Integer> KEPHRI_BOMB_SHADOW_GRAPHICS = Map.of( // Graphics ID -> tick delay
            1447, 4,
            1446, 3
    );

    private static final Set<Integer> KEPHRI_BOMB_GRAPHICS_ID = Set.of(2156, 2157, 2158, 2159);
    private static final int SWARM_HEAL_ANIMATION_ID = 9607;
    private static final String SWARM_NAME = "Scarab Swarm";
    private static final int KEPHRI_BOMB_PROJECTILE_ID = 2266;
    private static final int EXPLODING_SCARAB_PROJECTILE_ID = 2147;
    private static final int KEPHRI_DEAD_ID = 11722;

    private int swarmsHealing;
    //    private Set<WorldPoint> bombHitTiles = new HashSet<>();
//    private final Set<Projectile> trackedProjectiles = new HashSet<>();
    @Getter
    private final Map<Integer, Set<WorldPoint>> bombShadowTiles = new HashMap<>(); // activationTick -> set of positions
    private final Set<WorldPoint> bombHitTiles = new HashSet<>();

    public KephriDetector() {
        swarmsHealing = 0;
    }

    @Override
    public void cleanup() {
        swarmsHealing = 0;
//        trackedProjectiles.clear();
        bombShadowTiles.clear();
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

        if (bombHitTiles.contains(raider.getPreviousWorldLocation())) {
            mistakes.add(KEPHRI_BOMB);
        }

        return mistakes;
    }

    @Override
    public void afterDetect() {
        swarmsHealing = 0;
        bombHitTiles.clear();
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (bombShadowTiles.containsKey(client.getTickCount())) {
            Set<WorldPoint> hits = bombShadowTiles.remove(client.getTickCount());
            bombHitTiles.addAll(hits);
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
//        if (KEPHRI_BOMB_GRAPHICS_ID.contains(event.getGraphicsObject().getId())) {
////            bombHitTiles.add(getWorldPoint(event.getGraphicsObject()));
//        }

        int id = event.getGraphicsObject().getId();
        if (KEPHRI_BOMB_SHADOW_GRAPHICS.containsKey(id)) {
            bombShadowTiles.computeIfAbsent(
                    client.getTickCount() + KEPHRI_BOMB_SHADOW_GRAPHICS.get(id),
                    k -> new HashSet<>()).add(getWorldPoint(event.getGraphicsObject()));
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
    public void onProjectileMoved(ProjectileMoved event) {
//        if (trackedProjectiles.contains(event.getProjectile())) return;
//
//        if (event.getProjectile().getId() != KEPHRI_BOMB_PROJECTILE_ID ||
//                event.getProjectile().getId() != EXPLODING_SCARAB_PROJECTILE_ID) {
//            return;
//        }
//
//        WorldPoint target = WorldPoint.fromLocal(client, event.getProjectile().getTarget());
//        int ticksToActivate = event.getProjectile().getRemainingCycles() / CYCLES_PER_GAME_TICK;
//        int activationTick = client.getTickCount() + ticksToActivate;
//
//        if (event.getProjectile().getId() == EXPLODING_SCARAB_PROJECTILE_ID) {
//            GraphicsObjectCreated e;
////            e.getGraphicsObject().finished()
//        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        if (event.getNpc().getId() == KEPHRI_DEAD_ID) {
            shutdown(); // Shut down and clean up all state. Any incoming bombs shouldn't count as mistakes.
        }
    }
}