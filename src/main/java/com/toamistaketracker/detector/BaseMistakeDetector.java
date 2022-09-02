package com.toamistaketracker.detector;

import com.toamistaketracker.RaidState;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.ToaMistakeTrackerPlugin;
import lombok.Getter;
import lombok.NonNull;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;
import java.util.List;

/**
 * Interface for detecting mistakes during The Tombs of Amascut
 */
public abstract class BaseMistakeDetector {

    @Inject
    protected ToaMistakeTrackerPlugin plugin;

    @Inject
    protected Client client;

    @Inject
    protected ClientThread clientThread;

    @Inject
    protected EventBus eventBus;

    @Inject
    protected RaidState raidState;

    @Getter
    public boolean detectingMistakes;

    protected BaseMistakeDetector() {
        detectingMistakes = false;
    }

    /**
     * Used to tell a detector to start listening for events.
     */
    public void startup() {
        eventBus.register(this);
        clientThread.invokeLater(this::computeDetectingMistakes);
    }

    /**
     * Shutdown and cleanup state. This is always called when the plugin is shutdown, or when a detector is finished.
     */
    public void shutdown() {
        detectingMistakes = false;
        eventBus.unregister(this);
    }

    /**
     * Compute if the detector should start detecting mistakes. This is always called from the client thread on startup.
     * This allows for detectors to startup right away if the plugin is turned on mid-raid, for example, and they missed
     * their normal startup trigger.
     */
    protected abstract void computeDetectingMistakes();

    /**
     * Detects mistakes for the given raider.
     * This is called during handling the {@link net.runelite.api.events.GameTick} event, each tick.
     *
     * @param raider - The raider to detect mistakes for
     * @return The list of {@link ToaMistake} detected on this tick
     */
    public abstract List<ToaMistake> detectMistakes(@NonNull Raider raider);

    /**
     * This optional method allows detectors to handle some logic after all detectMistakes methods have been invoked
     * for this {@link net.runelite.api.events.GameTick}.
     */
    public void afterDetect() {
    }
}
