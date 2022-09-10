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

import static com.toamistaketracker.RaidRoom.SCARABAS_PUZZLE;

/**
 *
 */
@Slf4j
@Singleton
public class ScarabasPuzzleDetector extends BaseMistakeDetector {

    public ScarabasPuzzleDetector() {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public boolean isDetectingMistakes() {
        return raidState.getCurrentRoom() == SCARABAS_PUZZLE;
    }

    @Subscribe
    public void onRaidRoomChanged(RaidRoomChanged event) {
        if (event.getPrevRaidRoom() == SCARABAS_PUZZLE) {
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