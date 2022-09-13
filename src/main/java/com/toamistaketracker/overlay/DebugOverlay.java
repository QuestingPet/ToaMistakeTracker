package com.toamistaketracker.overlay;

import com.toamistaketracker.RaidState;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistakeTrackerConfig;
import com.toamistaketracker.detector.boss.WardensP3Detector;
import com.toamistaketracker.detector.tracker.DelayedHitTilesTracker;
import com.toamistaketracker.detector.MistakeDetectorManager;
import com.toamistaketracker.detector.boss.BabaDetector;
import com.toamistaketracker.detector.boss.KephriDetector;
import com.toamistaketracker.detector.boss.WardensP1P2Detector;
import com.toamistaketracker.detector.puzzle.ApmekenPuzzleDetector;
import com.toamistaketracker.detector.puzzle.CrondisPuzzleDetector;
import com.toamistaketracker.detector.tracker.InstantHitTilesTracker;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
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
    private final ToaMistakeTrackerConfig config;
    private MistakeDetectorManager mistakeDetectorManager;

    @Inject
    public DebugOverlay(@Named("developerMode") boolean developerMode,
                        Client client,
                        RaidState raidState,
                        ToaMistakeTrackerConfig config,
                        MistakeDetectorManager mistakeDetectorManager) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);

        this.config = config;
        this.developerMode = developerMode;
        this.client = client;
        this.raidState = raidState;
        this.mistakeDetectorManager = mistakeDetectorManager;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!developerMode) return null;
        if (!config.debugMode()) return null;

        CrondisPuzzleDetector crondisPuzzleDetector = mistakeDetectorManager
                .getMistakeDetector(CrondisPuzzleDetector.class);
        ApmekenPuzzleDetector apmekenPuzzleDetector = mistakeDetectorManager
                .getMistakeDetector(ApmekenPuzzleDetector.class);
        BabaDetector babaDetector = mistakeDetectorManager.getMistakeDetector(BabaDetector.class);
        KephriDetector kephriDetector = mistakeDetectorManager.getMistakeDetector(KephriDetector.class);
        WardensP1P2Detector wardensP1P2Detector = mistakeDetectorManager.getMistakeDetector(WardensP1P2Detector.class);
        WardensP3Detector wardensP3Detector = mistakeDetectorManager.getMistakeDetector(WardensP3Detector.class);

        for (WorldPoint worldPoint : crondisPuzzleDetector.getWaterFallTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.RED);
        }

        for (WorldPoint worldPoint : crondisPuzzleDetector.getPalmTreeTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.GREEN);
        }

        for (WorldPoint worldPoint : apmekenPuzzleDetector.getVolatileHitTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.RED);
        }

        for (WorldPoint worldPoint : babaDetector.getBoulderTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.RED);
        }

        renderHitTiles(graphics, babaDetector.getFallingBoulderHitTiles());
        renderHitTiles(graphics, babaDetector.getProjectileBoulderHitTiles());

        for (NPC npc : babaDetector.getRubbles()) {
            renderTile(graphics, toLocalPoint(npc.getWorldLocation()), Color.ORANGE);
        }

        babaDetector.getSafeRubbleTiles().values()
                .forEach(tiles -> tiles
                        .forEach(tile -> renderTile(graphics, toLocalPoint(tile), Color.GREEN)));

        for (WorldPoint worldPoint : babaDetector.getGapTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.GREEN);
        }

        renderHitTiles(graphics, kephriDetector.getBombHitTiles());

        for (GameObject gameObject : wardensP1P2Detector.getActivePyramids()) {
            renderTile(graphics, toLocalPoint(gameObject.getWorldLocation()), Color.RED);
        }

        renderHitTiles(graphics, wardensP1P2Detector.getPyramidHitTiles());

        renderHitTiles(graphics, wardensP1P2Detector.getDdrHitTiles());
        renderHitTiles(graphics, wardensP1P2Detector.getWindmillHitTiles());
        renderHitTiles(graphics, wardensP1P2Detector.getBombHitTiles());
        renderHitTiles(graphics, wardensP1P2Detector.getSpecialPrayerHitTiles());

        renderHitTiles(graphics, wardensP3Detector.getEarthquakeHitTiles());
        renderHitTiles(graphics, wardensP3Detector.getKephriBombHitTiles());
        renderHitTiles(graphics, wardensP3Detector.getBabaBoulderTiles());
        renderHitTiles(graphics, wardensP3Detector.getLightningHitTiles());

        for (Raider raider : raidState.getRaiders().values()) {
            if (raider.getPreviousWorldLocationForOverlay() != null) {
                LocalPoint localPoint = toLocalPoint(raider.getPreviousWorldLocationForOverlay());
                renderTile(graphics, localPoint, Color.MAGENTA);
            }
        }

        return null;
    }

    private void renderTile(final Graphics2D graphics, final LocalPoint dest, final Color color) {
        renderTileWithText(graphics, dest, color, null);
    }

    private void renderTileWithText(final Graphics2D graphics, final LocalPoint dest, final Color color, String text) {
        if (dest == null) {
            return;
        }

        final Polygon poly = Perspective.getCanvasTilePoly(client, dest);

        if (poly == null) {
            return;
        }

        OverlayUtil.renderPolygon(graphics, poly, color);

        if (text == null) {
            return;
        }

        Point textPoint = Perspective.getCanvasTextLocation(client, graphics, dest, text, 0);
        if (textPoint == null) {
            return;
        }

        OverlayUtil.renderTextLocation(graphics, textPoint, text, color);
    }

    private LocalPoint toLocalPoint(WorldPoint worldPoint) {
        return LocalPoint.fromWorld(client, worldPoint);
    }

    private void renderHitTiles(final Graphics2D graphics, DelayedHitTilesTracker tracker) {
        tracker.getDelayedHitTiles().forEach((key, value) ->
                value.forEach(tile -> renderTileWithText(graphics, toLocalPoint(tile), Color.RED, String.valueOf(key - client.getTickCount() + 1))));
        tracker.getActiveHitTiles().forEach(tile -> renderTile(graphics, toLocalPoint(tile), Color.GREEN));
    }

    private void renderHitTiles(final Graphics2D graphics, InstantHitTilesTracker tracker) {
        tracker.getActiveHitTiles().forEach(tile -> renderTile(graphics, toLocalPoint(tile), Color.GREEN));
    }
}