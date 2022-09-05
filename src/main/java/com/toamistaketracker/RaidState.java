package com.toamistaketracker;

import com.toamistaketracker.events.InRaidChanged;
import com.toamistaketracker.events.RaidEntered;
import com.toamistaketracker.events.RaidRoomChanged;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.toamistaketracker.RaidRoom.RAID_LOBBY_INSIDE;
import static com.toamistaketracker.RaidRoom.RAID_LOBBY_OUTSIDE;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RaidState {

    private static final int TOA_RAIDERS_VARC_START = 1099;
    private static final int MAX_RAIDERS = 8;

    private final Client client;
    private final EventBus eventBus;

    @Getter
    private boolean inRaid;
    @Getter
    private RaidRoom currentRoom;
    @Getter
    private Map<String, Raider> raiders; // name -> raider

    private int prevRegion;

    public void startUp() {
        clearState();
        eventBus.register(this);
    }

    public void shutDown() {
        clearState();
        eventBus.unregister(this);
    }

    private void clearState() {
        inRaid = false;
        currentRoom = null;
        raiders = new HashMap<>();
        prevRegion = -1;
    }

    @Subscribe(priority = 5)
    public void onGameTick(GameTick e) {
        if (client.getGameState() != GameState.LOGGED_IN) return;

        int newRegion = getRegion();
        if (newRegion == -1) return;

        log.debug("Current region: {}", newRegion);

        if (prevRegion != newRegion) {
            regionChanged(newRegion);
        }
        prevRegion = newRegion;

//        Widget widget = client.getWidget(WidgetInfo.TOA_RAID_LAYER);
//        log.debug("Widget is null: {}", widget == null);
//        log.debug("Widget is hidden: {}", widget != null && widget.isHidden());
//        boolean newInRaid = widget != null && !widget.isHidden();
        boolean newInRaid = currentRoom != null && currentRoom != RAID_LOBBY_OUTSIDE;
        if (newInRaid != inRaid) {
            log.debug("In Raid changed: {}", newInRaid);
            eventBus.post(new InRaidChanged(newInRaid));
        }

        // TODO: ALWAYS COMPUTE THE REGION. WHERE DO WE GO IN TRANSITIONS?!?!?
        inRaid = newInRaid;

        if (!inRaid) {
            log.debug("Not in raid");
            clearState();
            return;
        }

        log.debug("In raid");

        if (raiders.isEmpty()) {
            tryLoadRaiders();
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            // If there are still raiders, they can't be dead anymore after loading.
            for (Raider raider : raiders.values()) {
                raider.setDead(false);
            }
        }
    }

    public boolean isRaider(Actor actor) {
        return raiders.containsKey(actor.getName());
    }

    private int getRegion() {
        LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
        if (localPoint == null) {
            return -1;
        } else {
            return WorldPoint.fromLocalInstance(client, localPoint).getRegionID();
        }
    }

    private void regionChanged(int newRegion) {
        log.debug("Region changed");
        currentRoom = RaidRoom.forRegionId(newRegion);
        if (currentRoom == null) {
            return;
        }

        log.debug("New room: {}", currentRoom);
        RaidRoom prevRoom = RaidRoom.forRegionId(prevRegion);
        if (prevRoom == RAID_LOBBY_OUTSIDE && currentRoom == RAID_LOBBY_INSIDE) {
            newRaid();
        } else {
            log.debug("Raid room changed: {}", currentRoom);
            eventBus.post(RaidRoomChanged.builder().newRaidRoom(currentRoom).prevRaidRoom(prevRoom).build());
        }

        // TODO: RaidFinished?
    }

    private void newRaid() {
        log.debug("New raid");
        tryLoadRaiders();
        eventBus.post(new RaidEntered());
    }

    private void tryLoadRaiders() {
        log.debug("Setting raiders");
        raiders.clear();

        // TODO: Raiders seem to be VarClientStr 1099 start. Need to test with more people. And on DC
        Set<String> raiderNames = new HashSet<>(MAX_RAIDERS);
        for (int i = 0; i < MAX_RAIDERS; i++) {
            String name = client.getVarcStrValue(TOA_RAIDERS_VARC_START + i);
            if (name != null && !name.isEmpty()) {
                raiderNames.add(Text.sanitize(name));
            }
        }

        for (Player player : client.getPlayers()) {
            if (player != null &&
                    player.getName() != null &&
                    !raiders.containsKey(player.getName()) &&
                    raiderNames.contains(player.getName())) {
                raiders.put(player.getName(), new Raider(player));
            }
        }

        log.debug("Loaded raiders: {}", raiders.keySet());
    }
}
