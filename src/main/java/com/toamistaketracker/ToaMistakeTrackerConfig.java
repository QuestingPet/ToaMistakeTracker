package com.toamistaketracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(ToaMistakeTrackerPlugin.CONFIG_GROUP)
public interface ToaMistakeTrackerConfig extends Config {

    @ConfigItem(
            keyName = "showMistakesInChat",
            name = "Show Mistakes In Chat",
            description = "When a player makes a mistake in ToA, whether or not to log the mistake message to your " +
                    "public chat.",
            position = 0
    )
    default boolean showMistakesInChat() {
        return true;
    }

    @ConfigItem(
            keyName = "showMistakesOnOverheadText",
            name = "Show Mistakes On Overhead Text",
            description = "When a player makes a mistake in ToA, whether or not to show the mistake message above " +
                    "their head as overhead text.",
            position = 1
    )
    default boolean showMistakesOnOverheadText() {
        return true;
    }

    @ConfigItem(
            keyName = "stackQuestionMarks",
            name = "Stack Mistake Messages",
            description = "When a mistake message is set to \"?\", whether or not to stack the message based on " +
                    "the total number of mistakes made.",
            position = 2
    )
    default boolean stackQuestionMarks() {
        return true;
    }

    @ConfigItem(
            keyName = "stackOnlySameMistakes",
            name = "Stack Only Same Mistakes",
            description = "When \"Stack Mistake Messages\" is toggled on, whether or not the amount of times the " +
                    "mistake message stacks is based on the current mistake made or all raid mistakes.",
            position = 3
    )
    default boolean stackOnlySameMistakes() {
        return true;
    }

    @ConfigItem(
            keyName = "deathMessage",
            name = "Death",
            description = "Message to show on death.",
            position = 4
    )
    default String deathMessage() {
        return "I'm planking!";
    }

    @ConfigSection(
            name = "Akkha Mistake Messages",
            description = "Settings for the messages shown on Het puzzle and Akkha room mistakes (separate multiple messages by \"|\").",
            position = 5,
            closedByDefault = true
    )
    String hetAndAkkhaMistakeSettings = "hetAndAkkhaMistakeSettings";

    @ConfigItem(
            keyName = "hetPuzzleBeamMessage",
            name = "Het Puzzle Beam",
            description = "Message to show when hit by the beam in the Het puzzle room (separate multiple messages by \"|\").",
            section = hetAndAkkhaMistakeSettings,
            position = 0
    )
    default String hetPuzzleBeamMessage() {
        return "Ah! It burns!";
    }

    @ConfigItem(
            keyName = "hetPuzzleOrbMessage",
            name = "Het Puzzle Orb",
            description = "Message to show when hit by an orb in the Het puzzle room (separate multiple messages by \"|\").",
            section = hetAndAkkhaMistakeSettings,
            position = 1
    )
    default String hetPuzzleOrbMessage() {
        return "Embrace Darkness!";
    }

    @ConfigItem(
            keyName = "akkhaQuadrantMessage",
            name = "Akkha Quadrant",
            description = "Message to show when hit by a quadrant attack in the Akkha room (separate multiple messages by \"|\").",
            section = hetAndAkkhaMistakeSettings,
            position = 2
    )
    default String akkhaQuadrantMessage() {
        return "I'm too slow!";
    }

    @ConfigItem(
            keyName = "akkhaElementalOrbMessage",
            name = "Akkha Elemental Orb",
            description = "Message to show when hit by an elemental orb in the Akkha room (separate multiple messages by \"|\").",
            section = hetAndAkkhaMistakeSettings,
            position = 3
    )
    default String akkhaElementalOrbMessage() {
        return "I'm griefing!";
    }

    @ConfigItem(
            keyName = "akkhaUnstableOrbMessage",
            name = "Akkha Unstable Orb",
            description = "Message to show when hit by an unstable orb in the Akkha room (separate multiple messages by \"|\").",
            section = hetAndAkkhaMistakeSettings,
            position = 4
    )
    default String akkhaUnstableOrbMessage() {
        return "?";
    }

    @ConfigSection(
            name = "Zebak Mistake Messages",
            description = "Settings for the messages shown on Crondis puzzle and Zebak room mistakes (separate multiple messages by \"|\").",
            position = 6,
            closedByDefault = true
    )
    String crondisAndZebakMistakeSettings = "crondisAndZebakMistakeSettings";

    @ConfigItem(
            keyName = "crondisWaterMessage",
            name = "Crondis Water",
            description = "Message to show when depositing a less than full jug in the Crondis puzzle room (separate multiple messages by \"|\").",
            section = crondisAndZebakMistakeSettings,
            position = 0
    )
    default String crondisWaterMessage() {
        return "This jug feels a little light...";
    }

    @ConfigItem(
            keyName = "zebakAcidMessage",
            name = "Zebak Acid",
            description = "Message to show when hit by an acid pool in the Zebak room (separate multiple messages by \"|\").",
            section = crondisAndZebakMistakeSettings,
            position = 1
    )
    default String zebakAcidMessage() {
        return "I'm drowning in acid!";
    }

    @ConfigItem(
            keyName = "zebakBloodMessage",
            name = "Zebak Blood Cloud",
            description = "Message to show when hit by a blood cloud in the Zebak room (separate multiple messages by \"|\").",
            section = crondisAndZebakMistakeSettings,
            position = 2
    )
    default String zebakBloodMessage() {
        return "I'm on a blood cloud!";
    }

    @ConfigItem(
            keyName = "zebakScreamMessage",
            name = "Zebak Scream",
            description = "Message to show when hit by a scream attack in the Zebak room (separate multiple messages by \"|\").",
            section = crondisAndZebakMistakeSettings,
            position = 3
    )
    default String zebakScreamMessage() {
        return "Nihil!";
    }

    @ConfigItem(
            keyName = "zebakWaveMessage",
            name = "Zebak Wave",
            description = "Message to show when hit by a wave in the Zebak room (separate multiple messages by \"|\").",
            section = crondisAndZebakMistakeSettings,
            position = 4
    )
    default String zebakWaveMessage() {
        return "I'm surfing!";
    }

    @ConfigSection(
            name = "Kephri Mistake Messages",
            description = "Settings for the messages shown on Kephri room mistakes (separate multiple messages by \"|\").",
            position = 7,
            closedByDefault = true
    )
    String kephriMistakeSettings = "kephriMistakeSettings";

    @ConfigItem(
            keyName = "kephriBombMessage",
            name = "Kephri Bomb",
            description = "Message to show when hit by a bomb in the Kephri room (separate multiple messages by \"|\").",
            section = kephriMistakeSettings,
            position = 0
    )
    default String kephriBombMessage() {
        return "I'm exploding!";
    }

    @ConfigItem(
            keyName = "kephriSwarmMessage",
            name = "Kephri Swarm Heal",
            description = "Message to show when letting a medic swarm reach the boss in the Kephri room (separate multiple messages by \"|\").",
            section = kephriMistakeSettings,
            position = 1
    )
    default String kephriSwarmMessage() {
        return "The swarms are going in!";
    }

    @ConfigItem(
            keyName = "kephriEggMessage",
            name = "Kephri Hatching Egg",
            description = "Message to show when hit by a hatching egg in the Kephri room (separate multiple messages by \"|\").",
            section = kephriMistakeSettings,
            position = 2
    )
    default String kephriEggMessage() {
        return "I've been hatched!";
    }

    @ConfigSection(
            name = "Ba-Ba Mistake Messages",
            description = "Settings for the messages shown on Apmeken puzzle and Ba-Ba room mistakes (separate multiple messages by \"|\").",
            position = 8,
            closedByDefault = true
    )
    String apmekenAndBabaMistakeSettings = "apmekenAndBabaMistakeSettings";

    @ConfigItem(
            keyName = "apmekenVentMessage",
            name = "Apmeken Vent",
            description = "Message to show when failing the vents special in the Apmeken puzzle room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 0
    )
    default String apmekenVentMessage() {
        return "I'm fuming!";
    }

    @ConfigItem(
            keyName = "apmekenPillarMessage",
            name = "Apmeken Pillar",
            description = "Message to show when failing the pillars special in the Apmeken puzzle room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 1
    )
    default String apmekenPillarMessage() {
        return "The sky is falling!";
    }

    @ConfigItem(
            keyName = "apmekenCorruptionMessage",
            name = "Apmeken Corruption",
            description = "Message to show when failing the corruption special in the Apmeken puzzle room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 2
    )
    default String apmekenCorruptionMessage() {
        return "I've been corrupted!";
    }

    @ConfigItem(
            keyName = "apmekenVenomMessage",
            name = "Apmeken Venom",
            description = "Message to show when stepping on venom in the Apmeken puzzle room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 3
    )
    default String apmekenVenomMessage() {
        return "It's venomous!";
    }

    @ConfigItem(
            keyName = "apmekenVolatileMessage",
            name = "Apmeken Volatile",
            description = "Message to show when hit by a Volatile monkey in the Apmeken puzzle room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 4
    )
    default String apmekenVolatileMessage() {
        return "I'm exploding!";
    }

    @ConfigItem(
            keyName = "babaSlamMessage",
            name = "Ba-Ba Slam",
            description = "Message to show when hit by a slam attack in the Ba-Ba room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 5
    )
    default String babaSlamMessage() {
        return "Come on and slam!|And welcome to the jam!";
    }

    @ConfigItem(
            keyName = "babaProjectileBoulderMessage",
            name = "Ba-Ba Projectile Boulder",
            description = "Message to show when hit by a projectile boulder attack in the Ba-Ba room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 6
    )
    default String babaProjectileBoulderMessage() {
        return "I got rocked!";
    }

    @ConfigItem(
            keyName = "babaRollingBoulderMessage",
            name = "Ba-Ba Rolling Boulder",
            description = "Message to show when hit by a rolling boulder in the Ba-Ba room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 7
    )
    default String babaRollingBoulderMessage() {
        return "They see me rollin'...";
    }

    @ConfigItem(
            keyName = "babaFallingBoulderMessage",
            name = "Ba-Ba Falling Boulder",
            description = "Message to show when hit by a falling boulder in the Ba-Ba room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 8
    )
    default String babaFallingBoulderMessage() {
        return "It's raining!";
    }

    @ConfigItem(
            keyName = "babaBananaMessage",
            name = "Ba-Ba Banana",
            description = "Message to show when hit by a falling boulder in the Ba-Ba room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 9
    )
    default String babaBananaMessage() {
        return "Who put that there?";
    }

    @ConfigItem(
            keyName = "babaGapMessage",
            name = "Ba-Ba Gap",
            description = "Message to show when falling into the gap in the Ba-Ba room (separate multiple messages by \"|\").",
            section = apmekenAndBabaMistakeSettings,
            position = 10
    )
    default String babaGapMessage() {
        return "I'm going down!";
    }

    @ConfigSection(
            name = "Wardens Mistake Messages",
            description = "Settings for the messages shown on Warden room mistakes (separate multiple messages by \"|\").",
            position = 9,
            closedByDefault = true
    )
    String wardensMistakeSettings = "wardensMistakeSettings";

    @ConfigItem(
            keyName = "wardensPyramidMessage",
            name = "P1 Pyramids",
            description = "Message to show when hit by a pyramid during Wardens P1 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 0
    )
    default String wardensPyramidMessage() {
        return "I'm disco-ing!";
    }

    @ConfigItem(
            keyName = "wardensDDRMessage",
            name = "P2 DDR",
            description = "Message to show when hit by a DDR obelisk special during Wardens P2 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 1
    )
    default String wardensDDRMessage() {
        return "I'm dancing!";
    }

    @ConfigItem(
            keyName = "wardensWindmillMessage",
            name = "P2 Windmill",
            description = "Message to show when hit by a windmill obelisk special during Wardens P2 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 2
    )
    default String wardensWindmillMessage() {
        return "I'm winded!";
    }

    @ConfigItem(
            keyName = "wardensBombsMessage",
            name = "P2 Bombs",
            description = "Message to show when hit by the bombs obelisk special during Wardens P2 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 3
    )
    default String wardensBombsMessage() {
        return "I'm getting bombed!";
    }

    @ConfigItem(
            keyName = "wardensBindMessage",
            name = "P2 Bind",
            description = "Message to show when hit by the binding warden attack during Wardens P2 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 4
    )
    default String wardensBindMessage() {
        return "I'm in jail!";
    }

    @ConfigItem(
            keyName = "wardensPrayerMessage",
            name = "P2 Special Prayer",
            description = "Message to show when hit by the special prayer warden attack during Wardens P2 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 5
    )
    default String wardensPrayerMessage() {
        return "What even was that attack?";
    }

    @ConfigItem(
            keyName = "wardensSlamMessage",
            name = "P3 Slam",
            description = "Message to show when hit by the slam warden attack during Wardens P3 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 6
    )
    default String wardensSlamMessage() {
        return "I'm tripping!";
    }

    @ConfigItem(
            keyName = "wardensKephriMessage",
            name = "P3 Kephri",
            description = "Message to show when hit by a Kephri attack during Wardens P3 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 7
    )
    default String wardensKephriMessage() {
        return "I'm exploding!";
    }

    @ConfigItem(
            keyName = "wardensBabaMessage",
            name = "P3 Ba-Ba",
            description = "Message to show when hit by a Ba-Ba attack during Wardens P3 (separate multiple messages by \"|\").",
            section = wardensMistakeSettings,
            position = 8
    )
    default String wardensBabaMessage() {
        return "It's raining!";
    }
}
