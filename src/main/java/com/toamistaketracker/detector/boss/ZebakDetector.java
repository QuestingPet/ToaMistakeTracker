package com.toamistaketracker.detector.boss;

import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import com.toamistaketracker.events.RaidRoomChanged;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static com.toamistaketracker.RaidRoom.APMEKEN_PUZZLE;
import static com.toamistaketracker.RaidRoom.ZEBAK;

/**
 *
 */
@Slf4j
@Singleton
public class ZebakDetector extends BaseMistakeDetector {

    public ZebakDetector() {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public boolean isDetectingMistakes() {
        return raidState.getCurrentRoom() == ZEBAK;
    }

    @Subscribe
    public void onRaidRoomChanged(RaidRoomChanged event) {
        if (event.getPrevRaidRoom() == ZEBAK) {
            shutdown();
        }
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        List<ToaMistake> mistakes = new ArrayList<>();

        return mistakes;
    }

    @Override
    public void afterDetect() {
    }
}