package com.toamistaketracker.detector.tracker;

import lombok.NonNull;
import net.runelite.api.coords.WorldPoint;

import java.util.Set;

/**
 * Track hit tiles that should be activated at a specified {@link net.runelite.api.events.GameTick}
 */
public interface HitTilesTracker {

    /**
     * Sets the active hit tiles for the given game tick. This should be called once every GameTick update.
     *
     * @param gameTick the game tick
     */
    void activateHitTilesForTick(@NonNull Integer gameTick);

    /**
     * Retrieve the active hit tiles for this game tick
     *
     * @return The set of active hit tiles this game tick
     */
    Set<WorldPoint> getActiveHitTiles();

    /**
     * Clears all active hit tiles and other state
     */
    void clear();
}
