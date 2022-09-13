package com.toamistaketracker.detector.tracker;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class for a map tracking hit tiles that are active on the tick they are added
 */
@Slf4j
@Value
public class InstantHitTilesTracker implements HitTilesTracker {

    Set<WorldPoint> activeHitTiles = new HashSet<>();
    Set<WorldPoint> newHitTiles = new HashSet<>();

    /**
     * Add the specified hit tile to be activated this tick
     *
     * @param worldPoint The hit tile
     */
    public void addHitTile(@NonNull WorldPoint worldPoint) {
        newHitTiles.add(worldPoint);
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
        activeHitTiles.addAll(newHitTiles);
        newHitTiles.clear();
    }

    @Override
    public void clear() {
        activeHitTiles.clear();
        newHitTiles.clear();
    }
}
