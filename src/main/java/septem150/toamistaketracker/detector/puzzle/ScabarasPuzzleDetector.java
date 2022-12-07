package septem150.toamistaketracker.detector.puzzle;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import septem150.toamistaketracker.RaidRoom;
import septem150.toamistaketracker.Raider;
import septem150.toamistaketracker.ToaMistake;
import septem150.toamistaketracker.detector.BaseMistakeDetector;

import javax.inject.Singleton;

import static septem150.toamistaketracker.RaidRoom.SCABARAS_PUZZLE;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Slf4j
@Singleton
public class ScabarasPuzzleDetector extends BaseMistakeDetector {

    public ScabarasPuzzleDetector() {
    }

    @Override
    public void cleanup() {
    }

    @Override
    public RaidRoom getRaidRoom() {
        return SCABARAS_PUZZLE;
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
