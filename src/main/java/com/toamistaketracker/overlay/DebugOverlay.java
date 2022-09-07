package com.toamistaketracker.overlay;

import com.toamistaketracker.RaidState;
import com.toamistaketracker.Raider;
import com.toamistaketracker.detector.boss.AkkhaDetector;
import com.toamistaketracker.detector.puzzle.CrondisPuzzleDetector;
import com.toamistaketracker.detector.puzzle.HetPuzzleDetector;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import javax.inject.Named;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

/**
 * This is for testing with a visual aid
 */
@Slf4j
public class DebugOverlay extends Overlay {

    private final boolean developerMode;
    private final Client client;
    private final RaidState raidState;

    private final HetPuzzleDetector hetPuzzleDetector;
    private final CrondisPuzzleDetector crondisPuzzleDetector;
    private final AkkhaDetector akkhaDetector;

    @Inject
    public DebugOverlay(@Named("developerMode") boolean developerMode,
                        Client client,
                        RaidState raidState,
                        HetPuzzleDetector hetPuzzleDetector,
                        CrondisPuzzleDetector crondisPuzzleDetector,
                        AkkhaDetector akkhaDetector) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);

        this.developerMode = developerMode;
        this.client = client;
        this.raidState = raidState;

        this.hetPuzzleDetector = hetPuzzleDetector;
        this.crondisPuzzleDetector = crondisPuzzleDetector;
        this.akkhaDetector = akkhaDetector;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!developerMode) return null;

        for (Raider raider : raidState.getRaiders().values()) {
            if (raider.getPreviousWorldLocationForOverlay() != null) {
                LocalPoint localPoint = toLocalPoint(raider.getPreviousWorldLocationForOverlay());
                renderTile(graphics, localPoint, Color.MAGENTA);
            }
        }

        for (WorldPoint worldPoint : crondisPuzzleDetector.getWaterFallTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.RED);
        }

        for (WorldPoint worldPoint : crondisPuzzleDetector.getPalmTreeTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.GREEN);
        }

        return null;
    }

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color) {
        if (dest == null) {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

        if (poly == null) {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, color);
    }

    private LocalPoint toLocalPoint(WorldPoint worldPoint) {
        return LocalPoint.fromWorld(client, worldPoint);
    }
}