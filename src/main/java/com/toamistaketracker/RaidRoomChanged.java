package com.toamistaketracker;

import lombok.Value;

/**
 * An event where the current {@link RaidRoom} has changed in the {@link RaidState}.
 */
@Value
public class RaidRoomChanged {

    /**
     * The new RaidRoom
     */
    RaidRoom raidRoom;
}