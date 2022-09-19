package com.toamistaketracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(ToaMistakeTrackerPlugin.CONFIG_GROUP)
public interface ToaMistakeTrackerConfig extends Config {

    @ConfigItem(
            keyName = "showMistakesInChat",
            name = "Show Mistakes In Chat",
            description = "When a player makes a mistake in ToA, whether or not to log the mistake message to your " +
                    "public chat.",
            position = 1
    )
    default boolean showMistakesInChat() {
        return true;
    }

    @ConfigItem(
            keyName = "showMistakesOnOverheadText",
            name = "Show Mistakes On Overhead Text",
            description = "When a player makes a mistake in ToA, whether or not to show the mistake message above " +
                    "their head as overhead text.",
            position = 2
    )
    default boolean showMistakesOnOverheadText() {
        return true;
    }
}
