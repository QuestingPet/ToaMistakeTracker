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

import static com.toamistaketracker.RaidRoom.BABA;
import static com.toamistaketracker.RaidRoom.KEPHRI;

/**
 *
 */
@Slf4j
@Singleton
public class BabaDetector extends BaseMistakeDetector {

    public BabaDetector() {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public boolean isDetectingMistakes() {
        return raidState.getCurrentRoom() == BABA;
    }

    @Subscribe
    public void onRaidRoomChanged(RaidRoomChanged event) {
        if (event.getPrevRaidRoom() == BABA) {
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