package septem150.toamistaketracker.detector.tracker;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;
import septem150.toamistaketracker.RaidState;

import javax.inject.Inject;

@Slf4j
public abstract class BaseRaidTracker {

    @Inject
    protected Client client;
    @Inject
    protected EventBus eventBus;
    @Inject
    protected RaidState raidState;

    public void startup() {
        cleanup();
        log.debug("Starting tracker");
        eventBus.register(this);
    }

    public void shutdown() {
        log.debug("Shutting down tracker");
        eventBus.unregister(this);
        cleanup();
    }

    public abstract void afterDetect();

    public abstract void cleanup();
}
