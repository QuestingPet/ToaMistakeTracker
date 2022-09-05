package com.toamistaketracker.detector;

import com.google.common.annotations.VisibleForTesting;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.boss.AkkhaDetector;
import com.toamistaketracker.detector.puzzle.HetPuzzleDetector;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Manager for all the {@link BaseMistakeDetector}. It keeps all the detectors in memory in order to manage events.
 * <p>
 * All detectors initialized in the manager are responsible for determining when to start detecting mistakes.
 * The manager may call the startup() or shutdown() method on a detector at any time.
 * <p>
 * When the manager is on (started = true), then all other detectors are subscribed to the EventBus and
 * listening for events on when to turn themselves on/off. This will only be true while the player is in Tob.
 */
@Slf4j
@Singleton
public class MistakeDetectorManager {

    private final List<BaseMistakeDetector> mistakeDetectors;

    @Getter
    @VisibleForTesting
    private boolean started;

    @Inject
    public MistakeDetectorManager(AkkhaDetector akkhaDetector,
                                  HetPuzzleDetector hetPuzzleDetector) {
        // Order matters -- death should be last
        this.mistakeDetectors = new ArrayList<>(Arrays.asList(
                akkhaDetector,
                hetPuzzleDetector));
        this.started = false;
    }

    public void startup() {
        started = true;
        for (BaseMistakeDetector mistakeDetector : mistakeDetectors) {
            mistakeDetector.startup();
        }
    }

    public void shutdown() {
        started = false;
        for (BaseMistakeDetector mistakeDetector : mistakeDetectors) {
            mistakeDetector.shutdown();
        }
        // Don't clear mistakeDetectors or else we can't get them back.
    }

    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        if (!started) return List.of();

        List<ToaMistake> mistakes = new ArrayList<>();
        for (BaseMistakeDetector mistakeDetector : mistakeDetectors) {
            if (mistakeDetector.isDetectingMistakes() && !raider.isDead()) {
                mistakes.addAll(mistakeDetector.detectMistakes(raider));
            }
        }

        return mistakes;
    }

    public void afterDetect() {
        if (!started) return;

        for (BaseMistakeDetector mistakeDetector : mistakeDetectors) {
            if (mistakeDetector.isDetectingMistakes()) {
                mistakeDetector.afterDetect();
            }
        }
    }

    public List<BaseMistakeDetector> getMistakeDetectors() {
        return Collections.unmodifiableList(mistakeDetectors);
    }
}
