package com.toamistaketracker.mistakestate;

import com.toamistaketracker.ToaMistake;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Keeps track of mistakes for players
 */
class MistakeManager {

    private final Map<String, PlayerTrackingInfo> trackingInfo;
    private int trackedRaids;

    MistakeManager() {
        trackingInfo = new HashMap<>();
        trackedRaids = 0;
    }

    public void clearAllMistakes() {
        trackingInfo.clear();
        trackedRaids = 0;
    }

    public void addMistakeForPlayer(String playerName, ToaMistake mistake) {
        PlayerTrackingInfo playerInfo = trackingInfo.computeIfAbsent(playerName,
                k -> new PlayerTrackingInfo(playerName));
        playerInfo.incrementMistake(mistake);
    }

    public void newRaid(Set<String> playerNames) {
        // TODO: Small bug where if plugin is installed mid-raid (or mistakes reset), then player raids gets 1 but
        // total tracked raids is still 0
        trackedRaids++;

        for (String playerName : playerNames) {
            PlayerTrackingInfo playerInfo = trackingInfo.get(playerName);
            if (playerInfo != null) {
                playerInfo.incrementRaidCount();
            } else {
                trackingInfo.put(playerName, new PlayerTrackingInfo(playerName));
            }
        }
    }

    public void removeAllMistakesForPlayer(String playerName) {
        trackingInfo.remove(playerName);
    }

    public Set<String> getPlayersWithMistakes() {
        return trackingInfo.values().stream()
                .filter(PlayerTrackingInfo::hasMistakes)
                .map(PlayerTrackingInfo::getPlayerName)
                .collect(Collectors.toSet());
    }

    public int getMistakeCountForPlayer(String playerName, ToaMistake mistake) {
        PlayerTrackingInfo playerInfo = trackingInfo.get(playerName);
        if (playerInfo != null) {
            Integer count = playerInfo.getMistakes().get(mistake);
            if (count != null) {
                return count;
            }
        }

        return 0;
    }

    public int getTotalMistakeCountForAllPlayers() {
        // TODO: Fix bug where room death and raid death count as 2 distinct mistakes, but they're the same.
        int totalMistakes = 0;
        for (PlayerTrackingInfo playerInfo : trackingInfo.values()) {
            for (int mistakes : playerInfo.getMistakes().values()) {
                totalMistakes += mistakes;
            }
        }

        return totalMistakes;
    }

    public int getRaidCountForPlayer(String playerName) {
        PlayerTrackingInfo playerInfo = trackingInfo.get(playerName);
        if (playerInfo != null) {
            return playerInfo.getRaidCount();
        }

        return 0;
    }

    public int getTrackedRaids() {
        return trackedRaids;
    }
}
