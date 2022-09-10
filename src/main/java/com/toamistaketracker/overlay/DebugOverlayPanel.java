package com.toamistaketracker.overlay;

import com.toamistaketracker.RaidState;
import com.toamistaketracker.ToaMistakeTrackerConfig;
import com.toamistaketracker.ToaMistakeTrackerPlugin;
import com.toamistaketracker.detector.BaseMistakeDetector;
import com.toamistaketracker.detector.MistakeDetectorManager;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.inject.Named;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

/**
 * This is for testing with a visual aid
 */
public class DebugOverlayPanel extends OverlayPanel {

    private final static String OVERLAY_NAME = "Toa Mistake Tracker Overlay";

    private final Client client;
    private final RaidState raidState;
    private final ToaMistakeTrackerConfig config;

    private final MistakeDetectorManager mistakeDetectorManager;

    private final boolean developerMode;

    @Inject
    public DebugOverlayPanel(Client client, ToaMistakeTrackerPlugin plugin,
                             RaidState raidState,
                             ToaMistakeTrackerConfig config,
                             MistakeDetectorManager mistakeDetectorManager,
                             @Named("developerMode") boolean developerMode) {
        super(plugin);
        setPosition(OverlayPosition.TOP_RIGHT);
        setPriority(OverlayPriority.MED);

        this.client = client;
        this.raidState = raidState;
        this.config = config;
        this.mistakeDetectorManager = mistakeDetectorManager;
        this.developerMode = developerMode;

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, OVERLAY_NAME));
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY, "Reset", OVERLAY_NAME));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!developerMode) return null;
        if (!config.debugMode()) return null;

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Game Tick: " + client.getTickCount())
                .build());

        if (raidState.isInRaid()) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("In Raid")
                    .color(Color.GREEN)
                    .build());
        } else {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("NOT in raid")
                    .color(Color.RED)
                    .build());
        }

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Raiders:")
                .color(raidState.getRaiders().isEmpty() ? Color.RED : Color.GREEN)
                .build());

        raidState.getRaiders().keySet().forEach(name -> {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(name)
                    .build());
        });

        // Add all mistake detectors
        renderMistakeDetector(mistakeDetectorManager.getClass().getSimpleName(),
                mistakeDetectorManager.isStarted());
        for (BaseMistakeDetector mistakeDetector : mistakeDetectorManager.getMistakeDetectors()) {
            renderMistakeDetector(mistakeDetector.getClass().getSimpleName(), mistakeDetector.isDetectingMistakes());
        }

        return super.render(graphics);
    }

    private void renderMistakeDetector(String name, boolean isOn) {
        panelComponent.getChildren().add(LineComponent.builder()
                .left(name)
                .right(isOn ? "ON" : "OFF")
                .rightColor(isOn ? Color.GREEN : Color.RED)
                .build());
    }
}
