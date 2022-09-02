package com.toamistaketracker;

import com.google.inject.Provides;
import com.toamistaketracker.detector.MistakeDetectorManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    protected void startUp() throws Exception {
        clientThread.invoke(() -> {
            raidState.startUp();
            mistakeDetectorManager.startup();
        });
    }

    @Override
    protected void shutDown() throws Exception {
        raidState.shutDown();
        mistakeDetectorManager.shutdown();
    }

    private void checkRaidState() {
        if (client.getGameState() != GameState.LOGGED_IN) return;

        Widget widget = client.getWidget(WidgetInfo.TOA_RAID_LAYER);
        if (widget == null) return;



    }

    // This should run *after* all detectors have handled the GameTick.
    @Subscribe(priority = -1)
    public void onGameTick(GameTick event) {
        debugOverheadTicks();

        if (!raidState.isInRaid()) return;

//        if (!allRaidersLoaded) {
//            tryLoadRaiders();
//        }

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
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
        }
    }

    @Provides
    ToaMistakeTrackerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ToaMistakeTrackerConfig.class);
    }
}
