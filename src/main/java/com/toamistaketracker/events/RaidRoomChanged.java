package com.toamistaketracker.events;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.RaidState;
import lombok.Builder;
import lombok.Value;

/**
 * An event where the current {@link RaidRoom} has changed in the {@link RaidState}.
 */
@Value
@Builder
public class RaidRoomChanged {

    /**
     * The new RaidRoom
     */
    RaidRoom newRaidRoom;

    /**
     * The previous RaidRoom
     */
    RaidRoom prevRaidRoom;
}