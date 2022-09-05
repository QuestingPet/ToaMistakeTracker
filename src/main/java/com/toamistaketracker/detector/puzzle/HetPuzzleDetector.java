package com.toamistaketracker.detector.puzzle;

import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.AppliedHitsplatsTracker;
import com.toamistaketracker.detector.BaseMistakeDetector;
import com.toamistaketracker.events.RaidRoomChanged;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.toamistaketracker.RaidRoom.HET_PUZZLE;
import static com.toamistaketracker.ToaMistake.HET_PUZZLE_DARK_ORB;
import static com.toamistaketracker.ToaMistake.HET_PUZZLE_LIGHT;

/**
 * There are a few ways to detect an orb of darkness hitting a player. The easiest is probably to check if a graphics
 * ID of 379 spawns under the player's location. However, if there are multiple orbs that collide on the same tile,
 * only one graphics object gets created TODO: Confirm that.
 */
@Slf4j
@Singleton
public class HetPuzzleDetector extends BaseMistakeDetector {

    private static final Set<Integer> LIGHT_BEAM_GRAPHICS_IDS = Set.of(2064, 2114);
    private static final int ORB_OF_DARKNESS_GRAPHICS_ID = 379;

    @Getter
    private final Set<WorldPoint> orbHitTiles = new HashSet<>();
    @Getter
    private final Set<WorldPoint> lightHitTiles = new HashSet<>();

    private final AppliedHitsplatsTracker appliedHitsplats = new AppliedHitsplatsTracker();

    @Override
    public void cleanup() {
        orbHitTiles.clear();
        lightHitTiles.clear();
        appliedHitsplats.clear();
    }

    @Override
    public boolean isDetectingMistakes() {
        return raidState.getCurrentRoom() == HET_PUZZLE;
    }

    @Subscribe
    public void onRaidRoomChanged(RaidRoomChanged event) {
        if (event.getPrevRaidRoom() == HET_PUZZLE) {
            shutdown();
        }
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        log.debug("Het Detecting mistakes for {}", raider.getName());

        List<ToaMistake> mistakes = new ArrayList<>();

        log.debug("{} has hitsplats: {}", raider.getName(), appliedHitsplats.hasHitsplatApplied(raider.getName()));
        if (orbHitTiles.contains(raider.getPreviousWorldLocation()) &&
                appliedHitsplats.hasHitsplatApplied(raider.getName())) {
            mistakes.add(HET_PUZZLE_DARK_ORB);
            appliedHitsplats.removeHitsplatForRaider(raider.getName());
        }

        if (lightHitTiles.contains(raider.getPreviousWorldLocation())) {
            mistakes.add(HET_PUZZLE_LIGHT);
        }

        return mistakes;
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        LocalPoint localPoint = event.getGraphicsObject().getLocation();
        WorldPoint worldPoint = WorldPoint.fromLocal(client, localPoint);
        if (event.getGraphicsObject().getId() == ORB_OF_DARKNESS_GRAPHICS_ID) {
            orbHitTiles.add(worldPoint);
        } else if (LIGHT_BEAM_GRAPHICS_IDS.contains(event.getGraphicsObject().getId())) {
            lightHitTiles.add(worldPoint);
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if (raidState.isRaider(event.getActor())) {
            log.debug("Adding hitsplat for {}", event.getActor().getName());
            appliedHitsplats.addHitsplatForRaider(event.getActor().getName());
        }
    }
}
