package com.toamistaketracker;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RaidState {

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
        inRaid = false;
        raiders = new HashMap<>();
        prevRegion = -1;
        eventBus.register(this);
    }

    public void shutDown() {
        raiders.clear();
        eventBus.unregister(this);
    }

    @Subscribe(priority = 5)
    public void onGameTick(GameTick e) {
        if (client.getGameState() != GameState.LOGGED_IN) return;

        Widget widget = client.getWidget(WidgetInfo.TOA_RAID_LAYER);
        inRaid = widget != null && !widget.isHidden();

        if (!inRaid) {
            log.debug("Not in raid");
            currentRoom = null;
            prevRegion = -1;
            return;
        }

        log.debug("In raid");

        if (raiders.isEmpty()) {
            // TODO: When reset? Probably from lobby -> inRaid:true
            log.debug("Setting raiders");
            // Raiders seem to be VarClientStr 1099 start. Need to test with more people. And on DC
            raiders = client.getPlayers().stream()
                    .filter(p -> Objects.equals(p.getName(), "Questing Pet"))
                    .collect(Collectors.toMap(Actor::getName, Raider::new));
        }

        LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
        final int region;
        if (localPoint == null) {
            region = -1;
        } else {
            region = WorldPoint.fromLocalInstance(client, localPoint).getRegionID();
        }

        if (prevRegion != region) {
            currentRoom = RaidRoom.forRegionId(region);
            log.debug("New room: {}", currentRoom);
            if (currentRoom != null) {
                eventBus.post(new RaidRoomChanged(currentRoom));
            }
        }

        prevRegion = region;
    }
}
