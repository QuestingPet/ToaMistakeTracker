package com.toamistaketracker.detector;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for a map tracking applied hitsplats for a raider
 */
public class AppliedHitsplatsTracker {

    private final Map<String, Integer> appliedHitsplats = new HashMap<>(); // name -> # of hitsplats applied this tick

    /**
     * Since multiple players could be on this tile, confirm there's a corresponding hitsplat applied to that raider
     *
     * @param name The name of the raider
     * @return True if there was a hitsplat applied to the specified raider on this tick, else false
     */
    public boolean hasHitsplatApplied(String name) {
        return appliedHitsplats.containsKey(name) && appliedHitsplats.get(name) > 0;
    }

    public void addHitsplatForRaider(String name) {
        appliedHitsplats.compute(name, this::increment);
    }

    public void removeHitsplatForRaider(String name) {
        appliedHitsplats.compute(name, this::decrement);
    }

    public void clear() {
        appliedHitsplats.clear();
    }

    private Integer increment(String key, Integer value) {
        return value == null ? 1 : value + 1;
    }

    private Integer decrement(String key, Integer value) {
        return value == null ? -1 : value - 1;
    }
}
