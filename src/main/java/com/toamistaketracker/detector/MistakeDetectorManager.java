package com.toamistaketracker.detector;

import com.google.common.annotations.VisibleForTesting;
import com.toamistaketracker.RaidState;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.boss.AkkhaDetector;
import com.toamistaketracker.detector.boss.BabaDetector;
import com.toamistaketracker.detector.boss.KephriDetector;
import com.toamistaketracker.detector.boss.WardensDetector;
import com.toamistaketracker.detector.boss.ZebakDetector;
import com.toamistaketracker.detector.death.DeathDetector;
import com.toamistaketracker.detector.puzzle.ApmekenPuzzleDetector;
import com.toamistaketracker.detector.puzzle.CrondisPuzzleDetector;
import com.toamistaketracker.detector.puzzle.HetPuzzleDetector;
import com.toamistaketracker.detector.puzzle.ScabarasPuzzleDetector;
import com.toamistaketracker.events.RaidRoomChanged;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager for all the {@link BaseMistakeDetector}. It keeps all the detectors in memory in order to manage events.
 * <p>
 * All detectors initialized in the manager are responsible for determining when to start detecting mistakes.
 * The manager may call the startup() or shutdown() method on a detector at any time.
 * <p>
 * When the manager is on (started = true), then all other detectors are subscribed to the EventBus and
 * listening for events on when to turn themselves on/off. This will only be true while the player is in Toa.
 */
@Slf4j
@Singleton
public class MistakeDetectorManager {

    private final EventBus eventBus;
    private final RaidState raidstate;

    @Getter
    private final List<BaseMistakeDetector> mistakeDetectors;

    @Getter
    @VisibleForTesting
    private boolean started;

    @Inject
    public MistakeDetectorManager(EventBus eventBus,
                                  RaidState raidState,
                                  HetPuzzleDetector hetPuzzleDetector,
                                  CrondisPuzzleDetector crondisPuzzleDetector,
                                  ScabarasPuzzleDetector scabarasPuzzleDetector,
                                  ApmekenPuzzleDetector apmekenPuzzleDetector,
                                  AkkhaDetector akkhaDetector,
                                  ZebakDetector zebakDetector,
                                  KephriDetector kephriDetector,
                                  BabaDetector babaDetector,
                                  WardensDetector wardensDetector,
                                  DeathDetector deathDetector) {
        // Order matters, since it's last write wins for which mistake gets put on overhead text. Death should be last.
        this.mistakeDetectors = List.of(
                hetPuzzleDetector,
                crondisPuzzleDetector,
                scabarasPuzzleDetector,
                apmekenPuzzleDetector,
                akkhaDetector,
                zebakDetector,
                kephriDetector,
                babaDetector,
                wardensDetector,
                deathDetector
        );

        this.eventBus = eventBus;
        this.raidstate = raidState;
        this.started = false;
    }

    public void startup() {
        started = true;
        eventBus.register(this);

        // Startup any detectors that should be active in *all* rooms
        mistakeDetectors.stream().filter(d -> d.getRaidRoom() == null).forEach(BaseMistakeDetector::startup);
    }

    public void shutdown() {
        mistakeDetectors.forEach(BaseMistakeDetector::shutdown);
        // Don't clear mistakeDetectors or else we can't get them back.

        eventBus.unregister(this);
        started = false;
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

    @Subscribe
    public void onRaidRoomChanged(RaidRoomChanged event) {
        // Detectors that run in *all* rooms do not need to handle these events
        mistakeDetectors.stream().filter(d -> d.getRaidRoom() != null).forEach(detector -> {
            if (detector.getRaidRoom() == event.getNewRaidRoom()) {
                detector.startup();
            } else if (detector.getRaidRoom() == event.getPrevRaidRoom()) {
                detector.shutdown();
            }
        });
    }
}
