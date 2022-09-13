package com.toamistaketracker.detector.boss;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import com.toamistaketracker.detector.tracker.DelayedHitTilesTracker;
import com.toamistaketracker.detector.tracker.InstantHitTilesTracker;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.toamistaketracker.RaidRoom.WARDENS_P3;
import static com.toamistaketracker.ToaMistake.WARDENS_P3_BABA;
import static com.toamistaketracker.ToaMistake.WARDENS_P3_EARTHQUAKE;
import static com.toamistaketracker.ToaMistake.WARDENS_P3_KEPHRI;
import static com.toamistaketracker.ToaMistake.WARDENS_P3_LIGHTNING;

/**
 * Wardens P3 is actually one of the most straightforward rooms, especially now that I've written all of the other
 * detectors, trackers, etc. and am more familiar with the detection flow.
 *
 * Earthquakes are just graphics objects that start their animation at some point in the future, and so we make use of
 * our {@link DelayedHitTilesTracker} to track the tiles that activate after a delay.
 *
 * Kephri bombs create a graphics object on the tick that they detonate, so we just look for that
 *
 * Ba-Ba boulders are similar to how they work in their boss room. We delay the graphics object spawn by a set number
 * of ticks before detecting
 *
 * Finally, the lightning attack is similar to Kephri bombs in that it spawns a graphics object on the tick it detonates
 */
@Slf4j
@Singleton
public class WardensP3Detector extends BaseMistakeDetector {

    private final Set<Integer> EARTHQUAKE_GRAPHICS_IDS = Set.of(2220, 2221, 2222, 2223);
    private final Set<Integer> KEPHRI_BOMB_GRAPHICS_IDS = Set.of(2156, 2157, 2158, 2159);
    // Graphics ID -> tick delay
    private final Map<Integer, Integer> BABA_BOULDERS = Map.of(
            2250, 6,
            2251, 4
    );

    private static final int EARTHQUAKE_HIT_DELAY_IN_TICKS = 0;
    private static final int P3_LIGHTNING_HIT_DELAY_IN_TICKS = 0;

    private static final int P3_LIGHTNING_GRAPHICS_ID = 2197;

    @Getter
    private final DelayedHitTilesTracker earthquakeHitTiles = new DelayedHitTilesTracker();
    @Getter
    private final InstantHitTilesTracker kephriBombHitTiles = new InstantHitTilesTracker();
    @Getter
    private final DelayedHitTilesTracker babaBoulderTiles = new DelayedHitTilesTracker();
    @Getter
    private final InstantHitTilesTracker lightningHitTiles = new InstantHitTilesTracker();

    @Override
    public void cleanup() {
        earthquakeHitTiles.clear();
        kephriBombHitTiles.clear();
        babaBoulderTiles.clear();
        lightningHitTiles.clear();
    }

    @Override
    public RaidRoom getRaidRoom() {
        return WARDENS_P3;
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        List<ToaMistake> mistakes = new ArrayList<>();

        if (earthquakeHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P3_EARTHQUAKE);
        }

        if (kephriBombHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P3_KEPHRI);
        }

        if (babaBoulderTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P3_BABA);
        }

        if (lightningHitTiles.getActiveHitTiles().contains(raider.getPreviousWorldLocation())) {
            mistakes.add(WARDENS_P3_LIGHTNING);
        }

        return mistakes;
    }

    @Override
    public void afterDetect() {
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        earthquakeHitTiles.activateHitTilesForTick(client.getTickCount());
        kephriBombHitTiles.activateHitTilesForTick(client.getTickCount());
        babaBoulderTiles.activateHitTilesForTick(client.getTickCount());
        lightningHitTiles.activateHitTilesForTick(client.getTickCount());
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        int id = event.getGraphicsObject().getId();
        if (EARTHQUAKE_GRAPHICS_IDS.contains(id)) {
            int activationTick = getActivationTick(event.getGraphicsObject(), EARTHQUAKE_HIT_DELAY_IN_TICKS);
            earthquakeHitTiles.addHitTile(activationTick, getWorldPoint(event.getGraphicsObject()));
        } else if (KEPHRI_BOMB_GRAPHICS_IDS.contains(id)) {
            kephriBombHitTiles.addHitTile(getWorldPoint(event.getGraphicsObject()));
        } else if (BABA_BOULDERS.containsKey(id)) {
            int activationTick = getActivationTick(event.getGraphicsObject(), BABA_BOULDERS.get(id));
            babaBoulderTiles.addHitTile(activationTick, getWorldPoint(event.getGraphicsObject()));
        } else if (id == P3_LIGHTNING_GRAPHICS_ID) {
            lightningHitTiles.addHitTile(getWorldPoint(event.getGraphicsObject()));
        }
    }
}