package com.toamistaketracker;

import com.google.inject.Provides;
import com.toamistaketracker.detector.MistakeDetectorManager;
import com.toamistaketracker.detector.tracker.VengeanceTracker;
import com.toamistaketracker.events.InRaidChanged;
import com.toamistaketracker.events.RaidEntered;
import com.toamistaketracker.panel.ToaMistakeTrackerPanel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "Toa Mistake Tracker"
)
public class ToaMistakeTrackerPlugin extends Plugin {

    public static final int CYCLES_PER_GAME_TICK = Constants.GAME_TICK_LENGTH / Constants.CLIENT_TICK_LENGTH;

    static final String CONFIG_GROUP = "toaMistakeTracker";

    private static final int OVERHEAD_TEXT_TICK_TIMEOUT = 5;
    private static final int CYCLES_FOR_OVERHEAD_TEXT = OVERHEAD_TEXT_TICK_TIMEOUT * CYCLES_PER_GAME_TICK;

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
    private VengeanceTracker vengeanceTracker;

    @Inject
    private OverlayManager overlayManager;

    // UI fields
    @Inject
    private ClientToolbar clientToolbar;
    private final BufferedImage icon = ImageUtil.loadImageResource(ToaMistakeTrackerPlugin.class, "panel-icon.png");
    private ToaMistakeTrackerPanel panel;
    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        // Can't @Inject because we null it out in shutdown()
        panel = injector.getInstance(ToaMistakeTrackerPanel.class);

        // Add UI
        panel.loadHeaderIcon(icon);
        navButton = NavigationButton.builder()
                .tooltip("Toa Mistake Tracker")
                .icon(icon)
                .priority(5)
                .panel(panel)
                .build();
        clientToolbar.addNavigation(navButton);

        // Start raid state detection
        clientThread.invoke(() -> {
            raidState.startUp();
        });

        // Reload the panel with all loaded mistakes
        panel.reload();
    }

    @Override
    protected void shutDown() throws Exception {
        // Clear all state
        raidState.shutDown();
        mistakeDetectorManager.shutdown();

        // Remove UI
        clientToolbar.removeNavigation(navButton);
        panel = null;
    }

    // This should run *after* all detectors have handled the GameTick.
    @Subscribe(priority = -1)
    public void onGameTick(GameTick event) {
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
            log.debug(client.getTickCount() + " Found mistakes for " + raider.getName() + " - " + mistakes);

            for (ToaMistake mistake : mistakes) {
                // Handle special logic for deaths
                if (mistake == ToaMistake.DEATH) {
                    raider.setDead(true);
                }

                addChatMessageForMistake(raider, mistake);
                addMistakeToOverlayPanel(raider, mistake);
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

    private void addChatMessageForMistake(Raider raider, ToaMistake mistake) {
        int mistakeCount = panel.getCurrentMistakeCountForPlayer(raider.getName(), mistake);
        String msg = ToaMistake.getChatMessageForMistakeCount(mistake, mistakeCount);

        if (msg.isEmpty()) return;

        // Add to overhead text if config is enabled
        final Player player = raider.getPlayer();
        if (config.showMistakesOnOverheadText()) {
            String overheadText = msg;
            if (vengeanceTracker.didPopVengeance(raider)) {
                overheadText = VengeanceTracker.VENGEANCE_TEXT + " " + overheadText;
            }
            player.setOverheadText(overheadText);
            player.setOverheadCycle(CYCLES_FOR_OVERHEAD_TEXT);
        }

        // Add to chat box if config is enabled
        if (config.showMistakesInChat()) {
            client.addChatMessage(ChatMessageType.PUBLICCHAT, player.getName(), msg, null);
        }
    }

    private void addMistakeToOverlayPanel(Raider raider, ToaMistake mistake) {
        // Certain mistakes have their own detection and chat messages, but should be grouped together as one in the
        // tracker panel and written state.
        ToaMistake groupedMistake = ToaMistake.toGroupedMistake(mistake);
        SwingUtilities.invokeLater(() -> panel.addMistakeForPlayer(raider.getName(), groupedMistake));
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
    public void onRaidEntered(RaidEntered event) {
        panel.newRaid(event.getRaiderNames());
    }

    @Provides
    ToaMistakeTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ToaMistakeTrackerConfig.class);
    }
}
