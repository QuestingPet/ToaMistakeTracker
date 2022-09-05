package com.toamistaketracker.events;

import lombok.Value;

/**
 * An event denoting that the inRaid raid state has changed.
 */
@Value
public class InRaidChanged {

    boolean inRaid;
}