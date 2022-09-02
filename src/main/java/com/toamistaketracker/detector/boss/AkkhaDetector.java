package com.toamistaketracker.detector.boss;

import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.Raider;
import com.toamistaketracker.detector.BaseMistakeDetector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.List;

@Slf4j
@Singleton
public class AkkhaDetector extends BaseMistakeDetector {

    @Override
    protected void computeDetectingMistakes() {

    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        return null;
    }
}
