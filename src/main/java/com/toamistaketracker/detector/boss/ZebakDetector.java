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

    private static final int CHOMP_ANIMATION_ID = 9620;
    private static final int CHOMP_HIT_DELAY_IN_TICKS = 2;

    // chomp detect if you're in melee distance after the 2 tick delay? Not sure of a better way. Can he chomp multiple ppl? I think so
    // acid can just check for poison hitsplat tbh. Nvm cuz of natural poison tick.
    // wave check to see if your previous location is same as wave's WORLD(?) location TODO: What about in water?
    // acid is 45570,1,2,3,4,5,6 game object
    // wave is 11738 (Wave)
    // earthquake is 2184 graphics object
    // Blood Cloud can check for heal hitsplat and *all* raiders on that tile that tick


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