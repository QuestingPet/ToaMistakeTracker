package com.toamistaketracker.detector.puzzle;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.RaidRoomChanged;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static com.toamistaketracker.ToaMistake.HET_PUZZLE_DARK_ORB;
import static com.toamistaketracker.ToaMistake.HET_PUZZLE_LIGHT;

@Slf4j
@Singleton
public class HetPuzzleDetector extends BaseMistakeDetector {

    @Inject
    public HetPuzzleDetector() {
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    protected void computeDetectingMistakes() {
        if (!detectingMistakes && raidState.getCurrentRoom() == RaidRoom.HET_PUZZLE) {
            detectingMistakes = true;
        }
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        log.debug("Detecting mistakes for {}", raider.getName());
        return List.of(HET_PUZZLE_LIGHT, HET_PUZZLE_DARK_ORB);
    }

    @Subscribe
    public void onRaidRoomChanged(RaidRoomChanged e) {
        detectingMistakes = raidState.getCurrentRoom() == RaidRoom.HET_PUZZLE;
    }
}
