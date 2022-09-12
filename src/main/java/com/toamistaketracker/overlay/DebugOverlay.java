package com.toamistaketracker.overlay;

import com.toamistaketracker.RaidState;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistakeTrackerConfig;
import com.toamistaketracker.detector.DelayedHitTilesTracker;
import com.toamistaketracker.detector.MistakeDetectorManager;
import com.toamistaketracker.detector.boss.BabaDetector;
import com.toamistaketracker.detector.boss.KephriDetector;
import com.toamistaketracker.detector.boss.WardensDetector;
import com.toamistaketracker.detector.puzzle.ApmekenPuzzleDetector;
import com.toamistaketracker.detector.puzzle.CrondisPuzzleDetector;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
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
        WardensDetector wardensDetector = mistakeDetectorManager.getMistakeDetector(WardensDetector.class);

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

        renderDelayedHitTiles(graphics, babaDetector.getFallingBoulderHitTiles());
        renderDelayedHitTiles(graphics, babaDetector.getProjectileBoulderHitTiles());

        for (NPC npc : babaDetector.getRubbles()) {
            renderTile(graphics, toLocalPoint(npc.getWorldLocation()), Color.ORANGE);
        }

        babaDetector.getSafeRubbleTiles().values()
                .forEach(tiles -> tiles
                        .forEach(tile -> renderTile(graphics, toLocalPoint(tile), Color.GREEN)));

        for (WorldPoint worldPoint : babaDetector.getGapTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.GREEN);
        }

        renderDelayedHitTiles(graphics, kephriDetector.getBombHitTiles());

        for (GameObject gameObject : wardensDetector.getActivePyramids()) {
            renderTile(graphics, toLocalPoint(gameObject.getWorldLocation()), Color.RED);
        }

        for (WorldPoint worldPoint : wardensDetector.getPyramidHitTiles()) {
            renderTile(graphics, toLocalPoint(worldPoint), Color.GREEN);
        }

        renderDelayedHitTiles(graphics, wardensDetector.getDdrHitTiles());
        renderDelayedHitTiles(graphics, wardensDetector.getBombHitTiles());

        for (Raider raider : raidState.getRaiders().values()) {
            if (raider.getPreviousWorldLocationForOverlay() != null) {
                LocalPoint localPoint = toLocalPoint(raider.getPreviousWorldLocationForOverlay());
                renderTile(graphics, localPoint, Color.MAGENTA);
            }
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

    private void renderDelayedHitTiles(final Graphics2D graphics, DelayedHitTilesTracker tracker) {
        tracker.getDelayedHitTiles().values()
                .forEach(tiles -> tiles
                        .forEach(tile -> renderTile(graphics, toLocalPoint(tile), Color.RED)));
        tracker.getActiveHitTiles().forEach(tile -> renderTile(graphics, toLocalPoint(tile), Color.GREEN));
    }
}