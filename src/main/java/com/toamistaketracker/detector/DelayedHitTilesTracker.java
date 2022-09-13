package com.toamistaketracker.detector;

import com.google.common.annotations.VisibleForTesting;
import lombok.Getter;
import lombok.NonNull;
import net.runelite.api.coords.WorldPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper class for a map tracking hit tiles delayed for a specified activation tick
 */
// TODO: Move to tracker package and make a HitTilesTracker interface with this and InstantHitTilesTracker object.
public class DelayedHitTilesTracker {

    Logger log = LoggerFactory.getLogger(DelayedHitTilesTracker.class);

    @Getter
    private Set<WorldPoint> activeHitTiles = new HashSet<>();

    @Getter
    @VisibleForTesting
    private final Map<Integer, Set<WorldPoint>> delayedHitTiles = new HashMap<>(); // activationTick -> set of positions

    /**
     * Add the specified hit tile to be retrieved at the specified activation tick
     *
     * @param activationTick The game tick to retrieve the hit tile
     * @param worldPoint     The hit tile
     */
    public void addHitTile(@NonNull Integer activationTick, @NonNull WorldPoint worldPoint) {
        delayedHitTiles.computeIfAbsent(activationTick, k -> new HashSet<>()).add(worldPoint);
    }

    public void addHitTiles(@NonNull Integer activationTick, @NonNull Set<WorldPoint> worldPoints) {
        delayedHitTiles.computeIfAbsent(activationTick, k -> new HashSet<>()).addAll(worldPoints);
    }

    /**
     * Sets the active hit tiles for the given game tick, and removes retrieved hit tiles from the delayed map. This
     * should be called once every GameTick update.
     *
     * @param gameTick the game tick
     */
    public void activateHitTilesForTick(@NonNull Integer gameTick) {
        activeHitTiles.clear();
        if (delayedHitTiles.containsKey(gameTick)) {
            activeHitTiles = delayedHitTiles.remove(gameTick);
        }
    }

    /**
     * Clears all hit tiles
     */
    public void clear() {
        activeHitTiles.clear();
        delayedHitTiles.clear();
    }
}
