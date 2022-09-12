package com.toamistaketracker.detector.death;

import com.toamistaketracker.RaidRoom;
import com.toamistaketracker.Raider;
import com.toamistaketracker.ToaMistake;
import com.toamistaketracker.detector.BaseMistakeDetector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.toamistaketracker.ToaMistake.DEATH;

/**
 *
 */
@Slf4j
@Singleton
public class DeathDetector extends BaseMistakeDetector {

    private final Set<String> raiderDeaths;

    public DeathDetector() {
        raiderDeaths = new HashSet<>();
        log.debug("LOL RECALL");
    }

    @Override
    public void cleanup() {
        raiderDeaths.clear();
    }

    @Override
    public RaidRoom getRaidRoom() {
        return null; // null means *all* rooms
    }

    @Override
    public List<ToaMistake> detectMistakes(@NonNull Raider raider) {
        List<ToaMistake> mistakes = new ArrayList<>();

        if (raiderDeaths.contains(raider.getName())) {
            mistakes.add(DEATH);
        }

        return mistakes;
    }

    @Override
    public void afterDetect() {
        raiderDeaths.clear();
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if (!(event.getActor() instanceof Player)) return;

        if (raidState.isRaider(event.getActor()))
            raiderDeaths.add(event.getActor().getName());
    }
}