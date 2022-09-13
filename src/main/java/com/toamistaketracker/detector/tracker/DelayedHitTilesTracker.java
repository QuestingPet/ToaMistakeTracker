package com.toamistaketracker.detector.tracker;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper class for a map tracking hit tiles delayed for a specified activation tick
 */
@Slf4j
@Value
public class DelayedHitTilesTracker implements HitTilesTracker {

    Set<WorldPoint> activeHitTiles = new HashSet<>();

    Map<Integer, Set<WorldPoint>> delayedHitTiles = new HashMap<>(); // activationTick -> set of positions

    /**
     * Add the specified hit tile to be retrieved at the specified activation tick
     *
     * @param activationTick The game tick to retrieve the hit tile
     * @param worldPoint     The hit tile
     */
    public void addHitTile(@NonNull Integer activationTick, @NonNull WorldPoint worldPoint) {
        delayedHitTiles.computeIfAbsent(activationTick, k -> new HashSet<>()).add(worldPoint);
    }

    /**
     * Add the specified hit tiles to be retrieved at the specified activation tick
     *
     * @param activationTick The game tick to retrieve the hit tile
     * @param worldPoints    The hit tiles
     */
    public void addHitTiles(@NonNull Integer activationTick, @NonNull Set<WorldPoint> worldPoints) {
        delayedHitTiles.computeIfAbsent(activationTick, k -> new HashSet<>()).addAll(worldPoints);
    }

    /**
     * Sets the active hit tiles for the given game tick, and removes retrieved hit tiles from the delayed map. This
     * should be called once every GameTick update.
     *
     * @param gameTick the game tick
     */
    @Override
    public void activateHitTilesForTick(@NonNull Integer gameTick) {
        activeHitTiles.clear();
        if (delayedHitTiles.containsKey(gameTick)) {
            activeHitTiles.addAll(delayedHitTiles.remove(gameTick));
        }
    }

    @Override
    public void clear() {
        activeHitTiles.clear();
        delayedHitTiles.clear();
    }
}
