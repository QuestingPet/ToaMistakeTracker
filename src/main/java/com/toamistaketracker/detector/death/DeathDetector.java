package com.toamistaketracker.detector.death;

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

import static com.toamistaketracker.RaidRoom.KEPHRI;

/**
 *
 */
@Slf4j
@Singleton
public class DeathDetector extends BaseMistakeDetector {

    public DeathDetector() {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public boolean isDetectingMistakes() {
        return false;
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