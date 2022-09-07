package com.toamistaketracker.detector.puzzle;

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
import static com.toamistaketracker.RaidRoom.CRONDIS_PUZZLE;

/**
 *
 */
@Slf4j
@Singleton
public class ApmekenPuzzleDetector extends BaseMistakeDetector {

    public ApmekenPuzzleDetector() {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public boolean isDetectingMistakes() {
        return raidState.getCurrentRoom() == APMEKEN_PUZZLE;
    }

    @Subscribe
    public void onRaidRoomChanged(RaidRoomChanged event) {
        if (event.getPrevRaidRoom() == APMEKEN_PUZZLE) {
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