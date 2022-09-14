package com.toamistaketracker.mistakestate;

import com.toamistaketracker.ToaMistake;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulating class for relevant tracking information for a player, including mistakes.
 */
public class PlayerTrackingInfo {

    @NonNull
    @Getter
    private final String playerName;
    @NonNull
    @Getter
    private final Map<ToaMistake, Integer> mistakes;
    @Getter
    private int raidCount;

    public PlayerTrackingInfo(@NonNull final String playerName) {
        this.playerName = playerName;
        this.mistakes = new HashMap<>();
        this.raidCount = 1; // Default raid count is 1 since just by creating this object it's assumed there's a raid
    }

    public void incrementMistake(ToaMistake mistake) {
        mistakes.compute(mistake, PlayerTrackingInfo::increment);
    }

    public void incrementRaidCount() {
        raidCount++;
    }

    public boolean hasMistakes() {
        return !mistakes.isEmpty();
    }

    private static <T> Integer increment(T key, Integer oldValue) {
        return oldValue == null ? 1 : oldValue + 1;
    }
}
