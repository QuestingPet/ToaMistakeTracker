package com.toamistaketracker.detector.boss;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.toamistaketracker.RaidRoom.BABA;
import static com.toamistaketracker.ToaMistake.BABA_BANANA;
import static com.toamistaketracker.ToaMistake.BABA_GAP;
import static com.toamistaketracker.ToaMistake.BABA_ROLLING_BOULDER;
import static com.toamistaketracker.ToaMistake.BABA_SLAM;

/**
 * Ba-Ba is a mess. By far the hardest to detect mistakes for. Look at the docs for a different room instead.
 *
 * Oh man what a nightmare.
 *
 * The slam attack is simply just looking for the graphics object ID spawning under the player. Nice easy one here.
 *
 * For rolling boulders, it seems like there's no super consistent way to detect. The animation ID that the player
 * enacts is usually the most reliable, though it can be overwritten entirely by an attack animation (like fang) with
 * the right timing, or just by getting double hit by a boulder quickly. We could also check to see if the player
 * moves >2 tiles like in Zebak waves, but that's not always the case if you're already at the bottom edge (and can't
 * move anymore). The final option is to check if the player is colliding with the boulder NPC tiles, though there is
 * a delay until when the boulder can hit you again. Also the boulder despawns one tick too early for collision
 * detection against the low wall, so we have to extend it an extra tick too. Together, these should account for the
 * majority/all of cases.
 *
 * The banana slip is just looking for an AnimationChanged event on a raider for the slip animation. However, this can
 * be overridden by doing another animation (like attacking) as soon as the slip animation should be going off. To
 * account for this, we also check the graphic of the player during an animation change, to see if it's the slip
 * graphic. While this could *also* be overridden, it seems unlikely (if even possible) that neither of these will be
 * set at any point during the slip duration. To make sure we don't double count, we add a cooldown for when a player
 * can be slipping again after its most recent slip mistake.
 *
 * Finally, the rubble attack (where you have a large boulder falling onto you). This is the worst one by far, and is
 * currently unfinished.
 * It sets a graphics ID if it actually collides with a player (or object). This unfortunately can be bypassed by
 * overwriting on the same tick with a spec(like fang), which makes that an unreliable way of detection. Another way
 * is to check the projectile spawn, andcalculate when it would activate and hit the players, seemingly always 7
 * ticks later. Unfortunately, if Ba-Ba phase transitions while this is out, it's not trivial to detect as his
 * animation change sometimes happens *after* the projectile is set to hit the player. Additionally, even if we were
 * able to reliably detect when the boulder hits, we would still need to resolve which players made the mistake and
 * which got hit "correctly" by standing next to the rubble. If too many players are next to the same rubble, *all*
 * (or at least the number of "over" players) take damage. This likely will require maintaining the full state of the
 * game with rubble and simulating what the server would do, on the client.
 * This is something I may come back to and add in the future.
 */
@Slf4j
@Singleton
public class BabaDetector extends BaseMistakeDetector {

    // 7 ticks for falling boulder to hit you
    // What about if rubble takes damage *without* a graphics id? That means it must have been wiped due to phase
    // transition, and we can wipe all tracked projectiles.

    private static final Set<WorldPoint> GAP_REGION_TILES = Set.of(
            WorldPoint.fromRegion(BABA.getRegionId(), 20, 30, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 20, 31, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 20, 32, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 20, 33, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 20, 34, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 21, 30, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 21, 31, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 21, 32, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 21, 33, 0),
            WorldPoint.fromRegion(BABA.getRegionId(), 21, 34, 0)
    );

    private static final int BABA_SLAM_GRAPHICS_ID = 1103;
    private static final int BOULDER_ROLLED_ANIMATION_ID = 7210;
    private static final int GAP_FALLING_ANIMATION_ID = 4366;
    private static final int BANANA_GAME_OBJECT_ID = 45755; // If you 1-tick the banana it never even spawns :/
    private static final int BANANA_SLIP_ANIMATION_ID = 4030;
    private static final int BANANA_GRAPHICS_ID = 1575;
    private static final int BANANA_SLIP_COOLDOWN_IN_TICKS = 3;

    // The boulder collision doesn't really start for 1-2 ticks. Let's use 3 to be safe and rely on the other forms
    // of boulder detection in those early ticks anyway, as they should always be safe.
    private static final Integer BOULDER_SPAWN_DELAY_IN_TICKS = 3;
    private static final String BOULDER_NAME = "Boulder";

    @Getter
    private Set<WorldPoint> gapTiles;

    private final Set<WorldPoint> slamHitTiles;
    private final Set<String> raidersFell;
    private final Set<String> raidersSlipping;
    private final Map<String, Integer> raidersRecentlySlipped; // name -> tick they last slipped
    private final Set<String> raidersRolledAnimation;
    private final Set<String> raidersRolled;
    private final Set<String> raidersRolledLastTick;

    @Getter
    private final List<NPC> boulders;
    private final Map<Integer, List<NPC>> spawnedBoulders; // tick to spawn -> list of boulders
    private final List<NPC> despawnedBoulders;

    @Getter
    private final Set<WorldPoint> boulderTiles;
    private final Set<WorldPoint> finalBoulderTiles;

    public BabaDetector() {
        gapTiles = new HashSet<>();

        slamHitTiles = new HashSet<>();
        raidersFell = new HashSet<>();
        raidersSlipping = new HashSet<>();
        raidersRecentlySlipped = new HashMap<>();
        raidersRolledAnimation = new HashSet<>();
        raidersRolled = new HashSet<>();
        raidersRolledLastTick = new HashSet<>();

        boulders = new ArrayList<>();
        spawnedBoulders = new HashMap<>();
        despawnedBoulders = new ArrayList<>();
        boulderTiles = new HashSet<>();
        finalBoulderTiles = new HashSet<>();
    }

    @Override
    public void startup() {
        super.startup();
        computeGapTiles();
    }

    @Override
    public void cleanup() {
        gapTiles.clear();

        slamHitTiles.clear();
        raidersFell.clear();
        raidersSlipping.clear();
        raidersRecentlySlipped.clear();
        raidersRolledAnimation.clear();
        raidersRolled.clear();
        raidersRolledLastTick.clear();

        boulders.clear();
        spawnedBoulders.clear();
        despawnedBoulders.clear();
        boulderTiles.clear();
        finalBoulderTiles.clear();
    }

    @Override
    public RaidRoom getRaidRoom() {
        return BABA;
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        List<ToaMistake> mistakes = new ArrayList<>();

        if (slamHitTiles.contains(raider.getPreviousWorldLocation())) {
            mistakes.add(BABA_SLAM);
        }

        if (gapTiles.contains(raider.getPreviousWorldLocation()) && !raidersFell.contains(raider.getName())) {
            mistakes.add(BABA_GAP);
            raidersFell.add(raider.getName());
        }

        if (isSlip(raider)) {
            mistakes.add(BABA_BANANA);
            raidersRecentlySlipped.put(raider.getName(), client.getTickCount());
        }

        if (isRollingBoulder(raider)) {
            mistakes.add(BABA_ROLLING_BOULDER);
            raidersRolled.add(raider.getName());
        }

        return mistakes;
    }

    @Override
    public void afterDetect() {
        slamHitTiles.clear();
        raidersSlipping.clear();
        raidersRolledAnimation.clear();

        raidersRolledLastTick.clear();
        raidersRolledLastTick.addAll(raidersRolled);
        raidersRolled.clear();

        finalBoulderTiles.clear();
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (spawnedBoulders.containsKey(client.getTickCount())) {
            List<NPC> spawned = spawnedBoulders.remove(client.getTickCount());
            // Ignore boulders that have already despawned
            List<NPC> bouldersToSpawn = spawned.stream()
                    .filter(b -> !despawnedBoulders.remove(b))
                    .collect(Collectors.toList());
            boulders.addAll(bouldersToSpawn);
        }

        boulderTiles.clear();
        boulders.stream()
                .filter(b -> !b.isDead())
                .forEach(boulder -> boulderTiles.addAll(computeBoulderTiles(boulder.getWorldLocation())));
        boulderTiles.addAll(finalBoulderTiles);
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        if (event.getGraphicsObject().getId() == BABA_SLAM_GRAPHICS_ID) {
            slamHitTiles.add(getWorldPoint(event.getGraphicsObject()));
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        if (event.getNpc().getName() == null) return;

        String name = Text.removeTags(event.getNpc().getName());
        if (BOULDER_NAME.equals(name)) {
            spawnedBoulders
                    .computeIfAbsent(client.getTickCount() + BOULDER_SPAWN_DELAY_IN_TICKS, k -> new ArrayList<>())
                    .add(event.getNpc());
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        if (event.getNpc().getName() == null) return;

        String name = Text.removeTags(event.getNpc().getName());
        if (BOULDER_NAME.equals(name)) {
            despawnedBoulders.add(event.getNpc());
            boolean removed = boulders.remove(event.getNpc());
            if (removed && !event.getNpc().isDead()) {
                // This despawned from hitting the wall, not from a player killing it. Extend tiles for one more tick
                // Pretend the SW tile is 1 tile lower
                finalBoulderTiles.addAll(computeBoulderTiles(event.getNpc().getWorldLocation().dx(-1)));
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() == null || event.getActor().getName() == null) return;

        String name = Text.removeTags(event.getActor().getName());
        if (event.getActor() instanceof Player && raidState.isRaider(event.getActor())) {
            if (event.getActor().getAnimation() == BANANA_SLIP_ANIMATION_ID ||
                    event.getActor().getGraphic() == BANANA_GRAPHICS_ID) {
                raidersSlipping.add(name);
            } else if (event.getActor().getAnimation() == BOULDER_ROLLED_ANIMATION_ID) {
                raidersRolledAnimation.add(name);
            }
        }
    }

    private boolean isSlip(Raider raider) {
        if (!raidersSlipping.contains(raider.getName())) {
            return false;
        }

        boolean wasRecentlySlipped = raidersRecentlySlipped.containsKey(raider.getName()) &&
                client.getTickCount() - raidersRecentlySlipped.get(raider.getName()) <= BANANA_SLIP_COOLDOWN_IN_TICKS;

        return !wasRecentlySlipped;
    }

    private boolean isRollingBoulder(Raider raider) {
        if (raider.getPreviousWorldLocation() == null ||
                !isBoulderPhase() ||
                // Can't be rolled two ticks in a row
                raidersRolledLastTick.contains(raider.getName())) {
            return false;
        }

        // HACK
        // Normally a player cannot move > 2 tiles in 1 tick. The boulders often push you back more. Baba pushes you
        // back all the way to the wall when phase transition, but checking for boulder phase above handles that.
        // Always in negative X direction.
        boolean wasMovedFar = raider.getPreviousWorldLocation().getX() - raider.getCurrentWorldLocation().getX() > 2;

        // All of these together should catch the majority of cases
        return wasMovedFar ||
                raidersRolledAnimation.contains(raider.getName()) ||
                boulderTiles.contains(raider.getPreviousWorldLocation());
    }

    private boolean isBoulderPhase() {
        return !boulders.isEmpty() ||
                !spawnedBoulders.isEmpty() ||
                !boulderTiles.isEmpty() ||
                !finalBoulderTiles.isEmpty();
    }

    /**
     * Boulders are 3x3, so calculate them from the sw tile
     *
     * @param sw The sw tile of the boulder
     * @return The set of WorldPoints around and including the sw
     */
    private Set<WorldPoint> computeBoulderTiles(WorldPoint sw) {
        WorldPoint cw = sw.dy(1);
        WorldPoint nw = sw.dy(2);

        return Set.of(
                sw.dx(1), cw.dx(1), nw.dx(1),
                sw.dx(2), cw.dx(2), nw.dx(2),
                sw.dx(3), cw.dx(3), nw.dx(3));
    }

    private void computeGapTiles() {
        WorldPoint wpPlayer = client.getLocalPlayer().getWorldLocation();
        LocalPoint lpPlayer = LocalPoint.fromWorld(client, wpPlayer);
        if (lpPlayer == null) return;

        int dx = lpPlayer.getSceneX() - wpPlayer.getRegionX();
        int dy = lpPlayer.getSceneY() - wpPlayer.getRegionY();

        gapTiles = GAP_REGION_TILES.stream()
                .map(wp -> WorldPoint.fromScene(client, wp.getRegionX() + dx, wp.getRegionY() + dy, wp.getPlane()))
                .collect(Collectors.toSet());
    }
}