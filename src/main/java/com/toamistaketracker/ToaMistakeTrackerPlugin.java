package com.toamistaketracker;

import com.google.inject.Provides;
import com.toamistaketracker.detector.MistakeDetectorManager;
import com.toamistaketracker.events.InRaidChanged;
import com.toamistaketracker.events.RaidEntered;
import com.toamistaketracker.overlay.DebugOverlay;
import com.toamistaketracker.overlay.DebugOverlayPanel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
        name = "Toa Mistake Tracker"
)
public class ToaMistakeTrackerPlugin extends Plugin {

    static final String CONFIG_GROUP = "toaMistakeTracker";

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ToaMistakeTrackerConfig config;

    @Inject
    private EventBus eventBus;

    @Inject
    private MistakeDetectorManager mistakeDetectorManager;

    @Inject
    private RaidState raidState;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DebugOverlay debugOverlay;

    @Inject
    private DebugOverlayPanel debugOverlayPanel;

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(debugOverlay);
        overlayManager.add(debugOverlayPanel);

        clientThread.invoke(() -> {
            raidState.startUp();
        });
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(debugOverlay);
        overlayManager.remove(debugOverlayPanel);

        raidState.shutDown();
    }

    // This should run *after* all detectors have handled the GameTick.
    @Subscribe(priority = -1)
    public void onGameTick(GameTick event) {
        debugOverheadTicks();

        if (!raidState.isInRaid()) return;

        // Try detecting all possible mistakes for this GameTick
        detectAll();

        // Invoke post-processing method for detectors to get ready for the next GameTick
        afterDetectAll();
    }

    private void detectAll() {
        for (Raider raider : raidState.getRaiders().values()) {
            if (raider != null) {
                detect(raider);
            }
        }
    }

    private void detect(@NonNull Raider raider) {
        List<ToaMistake> mistakes = mistakeDetectorManager.detectMistakes(raider);
        if (!mistakes.isEmpty()) {
            log.debug("" + client.getTickCount() + " Found mistakes for " + raider.getName() + " - " + mistakes);

            for (ToaMistake mistake : mistakes) {
                // Handle special logic for deaths
                if (mistake == ToaMistake.DEATH) {
                    raider.setDead(true);
                }

//                addMistakeForPlayer(raider.getName(), mistake);
//                addChatMessageForPlayerMistake(raider.getPlayer(), mistake);
            }
        }

        afterDetect(raider);
    }

    private void afterDetect(Raider raider) {
        raider.setPreviousWorldLocationForOverlay(raider.getPreviousWorldLocation());
        raider.setPreviousWorldLocation(raider.getCurrentWorldLocation());
    }

    private void afterDetectAll() {
        mistakeDetectorManager.afterDetect();
    }

    private void debugOverheadTicks() {
        client.getPlayers().stream()
                .filter(p -> Objects.equals(p.getName(), "Questing Pet"))
                .findFirst()
                .ifPresent(p -> p.setOverheadText("" + client.getTickCount()));
    }

    @Subscribe
    public void onInRaidChanged(InRaidChanged e) {
        if (e.isInRaid()) {
            log.debug("Starting detectors");
            mistakeDetectorManager.startup();
        } else {
            log.debug("Shutting down detectors");
            mistakeDetectorManager.shutdown();
        }
    }

    @Subscribe
    public void onRaidEntered(RaidEntered e) {
        // TODO: Reset panel and state for current raid
    }

    @Provides
    ToaMistakeTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ToaMistakeTrackerConfig.class);
    }
}
