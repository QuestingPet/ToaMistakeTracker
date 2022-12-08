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
                    "public chat. Max message length of " + ToaMistake.MAX_MESSAGE_LENGTH + " to prevent spamming chat.",
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
            keyName = "mistakeMessageStacking",
            name = "Mistake Message Stacking",
            description = "When a player makes a mistake in ToA and the message is set to \"?\", how to handle " +
                    "repeated mistakes for the current raid.",
            position = 2
    )
    default StackingBehavior mistakeMessageStacking() {
        return StackingBehavior.SAME_MISTAKES_ONLY;
    }

    @ConfigSection(
            name = "Death Messages",
            description = "Settings for the messages shown on Death mistakes (separate multiple messages by \"|\").",
            position = 3,
            closedByDefault = true
    )
    String deathMistakeSettings = "deathMistakeSettings";

    @ConfigItem(
            keyName = "deathMessage",
            name = "Death",
            description = "Message to show on death.",
            section = deathMistakeSettings,
            position = 0
    )
    default String deathMessage() {
        return ToaMistake.DEATH.getDefaultMessage();
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
            description = "Message to show when hit by the beam in the Het puzzle room.",
            section = hetAndAkkhaMistakeSettings,
            position = 0
    )
    default String hetPuzzleBeamMessage() {
        return ToaMistake.HET_PUZZLE_LIGHT.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "hetPuzzleOrbMessage",
            name = "Het Puzzle Orb",
            description = "Message to show when hit by an orb in the Het puzzle room.",
            section = hetAndAkkhaMistakeSettings,
            position = 1
    )
    default String hetPuzzleOrbMessage() {
        return ToaMistake.HET_PUZZLE_DARK_ORB.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "akkhaQuadrantMessage",
            name = "Akkha Quadrant",
            description = "Message to show when hit by a quadrant attack in the Akkha room.",
            section = hetAndAkkhaMistakeSettings,
            position = 2
    )
    default String akkhaQuadrantMessage() {
        return ToaMistake.AKKHA_SPECIAL_QUADRANT_BOMB.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "akkhaElementalOrbMessage",
            name = "Akkha Elemental Orb",
            description = "Message to show when hit by an elemental orb in the Akkha room.",
            section = hetAndAkkhaMistakeSettings,
            position = 3
    )
    default String akkhaElementalOrbMessage() {
        return ToaMistake.AKKHA_SPECIAL_ELEMENTAL_ORBS.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "akkhaUnstableOrbMessage",
            name = "Akkha Unstable Orb",
            description = "Message to show when hit by an unstable orb in the Akkha room.",
            section = hetAndAkkhaMistakeSettings,
            position = 4
    )
    default String akkhaUnstableOrbMessage() {
        return ToaMistake.AKKHA_UNSTABLE_ORB.getDefaultMessage();
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
            description = "Message to show when depositing a less than full jug in the Crondis puzzle room.",
            section = crondisAndZebakMistakeSettings,
            position = 0
    )
    default String crondisWaterMessage() {
        return ToaMistake.CRONDIS_PUZZLE_LOW_WATER.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "zebakAcidMessage",
            name = "Zebak Acid",
            description = "Message to show when hit by an acid pool in the Zebak room.",
            section = crondisAndZebakMistakeSettings,
            position = 1
    )
    default String zebakAcidMessage() {
        return ToaMistake.ZEBAK_ACID_TILE.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "zebakBloodMessage",
            name = "Zebak Blood Cloud",
            description = "Message to show when hit by a blood cloud in the Zebak room.",
            section = crondisAndZebakMistakeSettings,
            position = 2
    )
    default String zebakBloodMessage() {
        return ToaMistake.ZEBAK_BLOOD_CLOUD.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "zebakScreamMessage",
            name = "Zebak Scream",
            description = "Message to show when hit by a scream attack in the Zebak room.",
            section = crondisAndZebakMistakeSettings,
            position = 3
    )
    default String zebakScreamMessage() {
        return ToaMistake.ZEBAK_EARTHQUAKE.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "zebakWaveMessage",
            name = "Zebak Wave",
            description = "Message to show when hit by a wave in the Zebak room.",
            section = crondisAndZebakMistakeSettings,
            position = 4
    )
    default String zebakWaveMessage() {
        return ToaMistake.ZEBAK_WAVE.getDefaultMessage();
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
            description = "Message to show when hit by a bomb in the Kephri room.",
            section = kephriMistakeSettings,
            position = 0
    )
    default String kephriBombMessage() {
        return ToaMistake.KEPHRI_BOMB.getDefaultMessage();
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
            description = "Message to show when failing the vents special in the Apmeken puzzle room.",
            section = apmekenAndBabaMistakeSettings,
            position = 0
    )
    default String apmekenVentMessage() {
        return ToaMistake.APMEKEN_PUZZLE_VENT.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "apmekenPillarMessage",
            name = "Apmeken Pillar",
            description = "Message to show when failing the pillars special in the Apmeken puzzle room.",
            section = apmekenAndBabaMistakeSettings,
            position = 1
    )
    default String apmekenPillarMessage() {
        return ToaMistake.APMEKEN_PUZZLE_PILLAR.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "apmekenCorruptionMessage",
            name = "Apmeken Corruption",
            description = "Message to show when failing the corruption special in the Apmeken puzzle room.",
            section = apmekenAndBabaMistakeSettings,
            position = 2
    )
    default String apmekenCorruptionMessage() {
        return ToaMistake.APMEKEN_PUZZLE_CORRUPTION.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "apmekenVenomMessage",
            name = "Apmeken Venom",
            description = "Message to show when stepping on venom in the Apmeken puzzle room.",
            section = apmekenAndBabaMistakeSettings,
            position = 3
    )
    default String apmekenVenomMessage() {
        return ToaMistake.APMEKEN_PUZZLE_VENOM.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "apmekenVolatileMessage",
            name = "Apmeken Volatile",
            description = "Message to show when hit by a Volatile monkey in the Apmeken puzzle room.",
            section = apmekenAndBabaMistakeSettings,
            position = 4
    )
    default String apmekenVolatileMessage() {
        return ToaMistake.APMEKEN_PUZZLE_VOLATILE.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "babaSlamMessage",
            name = "Ba-Ba Slam",
            description = "Message to show when hit by a slam attack in the Ba-Ba room.",
            section = apmekenAndBabaMistakeSettings,
            position = 5
    )
    default String babaSlamMessage() {
        return ToaMistake.BABA_SLAM.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "babaProjectileBoulderMessage",
            name = "Ba-Ba Projectile Boulder",
            description = "Message to show when hit by a projectile boulder attack in the Ba-Ba room.",
            section = apmekenAndBabaMistakeSettings,
            position = 6
    )
    default String babaProjectileBoulderMessage() {
        return ToaMistake.BABA_PROJECTILE_BOULDER.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "babaRollingBoulderMessage",
            name = "Ba-Ba Rolling Boulder",
            description = "Message to show when hit by a rolling boulder in the Ba-Ba room.",
            section = apmekenAndBabaMistakeSettings,
            position = 7
    )
    default String babaRollingBoulderMessage() {
        return ToaMistake.BABA_ROLLING_BOULDER.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "babaFallingBoulderMessage",
            name = "Ba-Ba Falling Boulder",
            description = "Message to show when hit by a falling boulder in the Ba-Ba room.",
            section = apmekenAndBabaMistakeSettings,
            position = 8
    )
    default String babaFallingBoulderMessage() {
        return ToaMistake.BABA_FALLING_BOULDER.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "babaBananaMessage",
            name = "Ba-Ba Banana",
            description = "Message to show when slipping on a banana in the Ba-Ba room.",
            section = apmekenAndBabaMistakeSettings,
            position = 9
    )
    default String babaBananaMessage() {
        return ToaMistake.BABA_BANANA.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "babaGapMessage",
            name = "Ba-Ba Gap",
            description = "Message to show when falling into the gap in the Ba-Ba room.",
            section = apmekenAndBabaMistakeSettings,
            position = 10
    )
    default String babaGapMessage() {
        return ToaMistake.BABA_GAP.getDefaultMessage();
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
            description = "Message to show when hit by a pyramid during Wardens P1.",
            section = wardensMistakeSettings,
            position = 0
    )
    default String wardensPyramidMessage() {
        return ToaMistake.WARDENS_P1_PYRAMID.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "wardensDDRMessage",
            name = "P2 DDR",
            description = "Message to show when hit by a DDR obelisk special during Wardens P2.",
            section = wardensMistakeSettings,
            position = 1
    )
    default String wardensDDRMessage() {
        return ToaMistake.WARDENS_P2_DDR.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "wardensWindmillMessage",
            name = "P2 Windmill",
            description = "Message to show when hit by a windmill obelisk special during Wardens P2.",
            section = wardensMistakeSettings,
            position = 2
    )
    default String wardensWindmillMessage() {
        return ToaMistake.WARDENS_P2_WINDMILL.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "wardensBombsMessage",
            name = "P2 Bombs",
            description = "Message to show when hit by the bombs obelisk special during Wardens P2.",
            section = wardensMistakeSettings,
            position = 3
    )
    default String wardensBombsMessage() {
        return ToaMistake.WARDENS_P3_KEPHRI.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "wardensBindMessage",
            name = "P2 Bind",
            description = "Message to show when hit by the binding warden attack during Wardens P2.",
            section = wardensMistakeSettings,
            position = 4
    )
    default String wardensBindMessage() {
        return ToaMistake.WARDENS_P2_BIND.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "wardensPrayerMessage",
            name = "P2 Special Prayer",
            description = "Message to show when hit by the special prayer warden attack during Wardens P2.",
            section = wardensMistakeSettings,
            position = 5
    )
    default String wardensPrayerMessage() {
        return ToaMistake.WARDENS_P2_SPECIAL_PRAYER.getDefaultMessage();
    }

    @ConfigItem(
            keyName = "wardensSlamMessage",
            name = "P3 Slam",
            description = "Message to show when hit by the slam warden attack during Wardens P3.",
            section = wardensMistakeSettings,
            position = 6
    )
    default String wardensSlamMessage() {
        return ToaMistake.WARDENS_P3_EARTHQUAKE.getDefaultMessage();
    }
}
