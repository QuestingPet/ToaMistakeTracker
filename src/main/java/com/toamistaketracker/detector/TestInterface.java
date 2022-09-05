package com.toamistaketracker.detector;

import com.toamistaketracker.events.InRaidChanged;

public interface TestInterface {



    void doThing();

    default void doOtherThing() {
    }

}
