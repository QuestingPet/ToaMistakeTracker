package com.toamistaketracker.detector.boss;

import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.AppliedHitsplatsTracker;
import com.toamistaketracker.detector.BaseMistakeDetector;
import com.toamistaketracker.events.RaidRoomChanged;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.toamistaketracker.RaidRoom.AKKHA;
import static com.toamistaketracker.ToaMistake.AKKHA_QUADRANT_SPECIAL;

@Slf4j
@Singleton
public class AkkhaDetector extends BaseMistakeDetector {

    private static final Set<Integer> QUADRANT_SPECIALS_GRAPHICS_IDS = Set.of(2256, 2257, 2258, 2259);
    private static final String UNSTABLE_ORB = "Unstable Orb"; // TODO: Do we need to look at the previous tile of the orb?

    @Getter
    private final Set<WorldPoint> quadrantSpecialTiles = new HashSet<>();

    private final AppliedHitsplatsTracker appliedHitsplats = new AppliedHitsplatsTracker();

    @Override
    public void cleanup() {
        quadrantSpecialTiles.clear();
        appliedHitsplats.clear();
    }

    @Override
    public boolean isDetectingMistakes() {
        return raidState.getCurrentRoom() == AKKHA;
    }

    @Subscribe
    public void onRaidRoomChanged(RaidRoomChanged event) {
        if (event.getPrevRaidRoom() == AKKHA) {
            shutdown();
        }
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        log.debug("Akkha Detecting mistakes for {}", raider.getName());

        List<ToaMistake> mistakes = new ArrayList<>();

        if (quadrantSpecialTiles.contains(raider.getPreviousWorldLocation())) {
            mistakes.add(AKKHA_QUADRANT_SPECIAL);
        }

        return mistakes;
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        if (QUADRANT_SPECIALS_GRAPHICS_IDS.contains(event.getGraphicsObject().getId())) {
            LocalPoint localPoint = event.getGraphicsObject().getLocation();
            WorldPoint worldPoint = WorldPoint.fromLocal(client, localPoint);
            quadrantSpecialTiles.add(worldPoint);
        }
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if (raidState.isRaider(event.getActor())) {
            appliedHitsplats.addHitsplatForRaider(event.getActor().getName());
        }
    }
}
