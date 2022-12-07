package septem150.toamistaketracker.events;

import lombok.Builder;
import lombok.Value;
import septem150.toamistaketracker.RaidRoom;
import septem150.toamistaketracker.RaidState;

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
