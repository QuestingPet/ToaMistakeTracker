package com.toamistaketracker;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

import java.util.Set;

/**
 * Encapsulating class for a {@link Player} and other relevant metadata in a raid.
 */
public class Raider {

    private static final Set<Integer> GHOST_POSE_IDS = ImmutableSet.of(5538, 5539);

    @Getter
    @NonNull
    private final Player player;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private WorldPoint previousWorldLocation;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private WorldPoint previousWorldLocationForOverlay;

    @Setter(AccessLevel.PACKAGE)
    private boolean isDead;

    Raider(@NonNull Player player) {
        this.player = player;
    }

    public String getName() {
        return player.getName();
    }

    public WorldPoint getCurrentWorldLocation() {
        return player.getWorldLocation();
    }

    public boolean isDead() {
        // If the plugin is turned off and on, or just as a safety net, also check to see if we're a ghost
        return isDead || GHOST_POSE_IDS.contains(player.getPoseAnimation());
    }
}
