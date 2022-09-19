package com.toamistaketracker;

import lombok.Getter;
import lombok.NonNull;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.Set;

public enum ToaMistake {
    // Deaths
    DEATH("Death", "", "death.png"), // Chat message handled in deaths below
    DEATH_HET("Path of Het Death", "I'm planking!", "death-akkha.png"),
    DEATH_CRONDIS("Path of Crondis Death", "I'm planking!", "death-zebak.png"),
    DEATH_SCABARAS("Path of Scabaras Death", "I'm planking!", "death-kephri.png"),
    DEATH_APMEKEN("Path of Apmeken Death", "I'm planking!", "death-baba.png"),
    DEATH_WARDENS("Wardens Death", "I'm planking!", "death-wardens.png"),

    // Het
    HET_PUZZLE_LIGHT("Het Puzzle Energy Beam", "Ah! It burns!", "het-light.png"),
    HET_PUZZLE_DARK_ORB("Het Puzzle Dark Orb", "Embrace Darkness!", "het-dark-orb2.png"),
    AKKHA_SPECIAL_QUADRANT_BOMB("Akkha Quadrant Bombs", "I'm too slow!", "akkha-quadrant3.png"),
    AKKHA_SPECIAL_ELEMENTAL_ORBS("Akkha Elemental Orbs", "I'm griefing!", "akkha-elemental2.png"),
    AKKHA_UNSTABLE_ORB("Akkha Unstable Orb", "?", "akkha-unstable-orb.png"),

    // Crondis
    CRONDIS_PUZZLE_LOW_WATER("Path of Crondis Watering", "This jug feels a little light...", "crondis-water.png"),
    ZEBAK_ACID_TILE("Zebak Acid Tile", "I'm drowning in acid!", "zebak-acid.png"),
    ZEBAK_BLOOD_CLOUD("Zebak Blood Cloud", "I'm on a blood cloud!", "zebak-blood-cloud.png"),
    ZEBAK_EARTHQUAKE("Zebak Scream", "Nihil!", "zebak-scream.png"),
    ZEBAK_WAVE("Zebak Wave", "I'm surfing!", "zebak-wave.png"),

    // Scabaras
    KEPHRI_BOMB("Kephri Bomb", "I'm exploding!", "kephri-bomb.png"),
    KEPHRI_SWARM_HEAL("Kephri Swarm Heal", "The swarms are going in!", "kephri-swarm.png"),
    KEPHRI_EGG_EXPLODE("Kephri Egg Explode", "I've been hatched!", ""),

    // Apmeken
    APMEKEN_PUZZLE_SIGHT("Apmeken Sight", "", "apmeken-sight.png"),
    APMEKEN_PUZZLE_VENT("Apmeken Vent", "I'm fuming!", ""),
    APMEKEN_PUZZLE_PILLAR("Apmeken Pillar", "The sky is falling!", ""),
    APMEKEN_PUZZLE_CORRUPTION("Apmeken Corruption", "I've been corrupted!", ""),
    APMEKEN_PUZZLE_VENOM("Apmeken Venom Tile", "It's venomous!", "apmeken-venom.png"),
    APMEKEN_PUZZLE_VOLATILE("Apmeken Volatile", "I'm exploding!", "apmeken-volatile.png"),
    BABA_SLAM("Ba-Ba Slam", "Come on and slam!", "baba-slam.png"),
    BABA_PROJECTILE_BOULDER("Ba-Ba Projectile Boulder", "I got rocked!", "baba-projectile-boulder.png"),
    BABA_ROLLING_BOULDER("Ba-Ba Rolling Boulder", "They see me rollin'...", "baba-rolling-boulder.png"),
    BABA_FALLING_BOULDER("Ba-Ba Falling Boulder", "It's raining!", "baba-falling-boulder.png"),
    BABA_BANANA("Ba-Ba Banana", "Who put that there?", "baba-banana.png"),
    BABA_GAP("Ba-Ba Gap", "I'm going down!", "baba-gap.png"),

    // Wardens
    WARDENS_P1_PYRAMID("Wardens P1 Pyramid", "I'm disco-ing!", "wardens-pyramid.png"),
    WARDENS_P2_OBELISK("Wardens P2 Obelisk", "", "wardens-obelisk.png"),
    WARDENS_P2_DDR("Wardens P2 DDR", "I'm dancing!", ""),
    WARDENS_P2_WINDMILL("Wardens P2 Windmill", "I'm winded!", ""),
    WARDENS_P2_BOMBS("Wardens P2 Bombs", "I'm getting bombed!", ""),
    WARDENS_P2_BIND("Wardens P2 Bind", "I'm in jail!", "wardens-bind.png"),
    WARDENS_P2_SPECIAL_PRAYER("Wardens P2 Special Prayer", "What even was that attack?", "wardens-special-prayer.png"),
    WARDENS_P3_EARTHQUAKE("Wardens P3 Slam", "I'm tripping!", "wardens-earthquake.png"),
    WARDENS_P3_AKKHA("Wardens P3 Akkha", "", "wardens-akkha.png"),
    WARDENS_P3_ZEBAK("Wardens P3 Zebak", "", "wardens-zebak.png"),
    WARDENS_P3_KEPHRI("Wardens P3 Kephri", "I'm exploding!", "wardens-kephri.png"),
    WARDENS_P3_BABA("Wardens P3 Ba-Ba", "It's raining!", "wardens-baba.png"),
    WARDENS_P3_LIGHTNING("Wardens P3 Lightning", "", "wardens-lightning.png"), // Too noisy
    ;

    private static final Set<ToaMistake> ROOM_DEATHS = EnumSet.of(
            DEATH_HET, DEATH_CRONDIS, DEATH_SCABARAS, DEATH_APMEKEN, DEATH_WARDENS);

    private static final Set<ToaMistake> APMEKEN_SIGHT_MISTAKES = EnumSet.of(
            APMEKEN_PUZZLE_VENT, APMEKEN_PUZZLE_PILLAR, APMEKEN_PUZZLE_CORRUPTION);
    private static final Set<ToaMistake> WARDENS_P2_OBELISK_MISTAKES = EnumSet.of(
            WARDENS_P2_DDR, WARDENS_P2_WINDMILL, WARDENS_P2_BOMBS);

    private static final String FALLBACK_IMAGE_PATH = "death.png";

    private static final int MAX_AKKHA_ORBS_CHAT_MESSAGE_LENGTH = 10;
    private static final String ALTERNATE_BABA_SLAM_CHAT_MESSAGE = "And welcome to the jam!";

    @Getter
    @NonNull
    private final String mistakeName;

    @Getter
    @NonNull
    private final String chatMessage;

    @Getter
    @NonNull
    private final BufferedImage mistakeImage;

    ToaMistake(@NonNull String mistakeName, @NonNull String chatMessage, @NonNull String mistakeImagePath) {
        this.mistakeName = mistakeName;
        this.chatMessage = chatMessage;

        final String imagePath;
        if (mistakeImagePath.isEmpty()) {
            imagePath = FALLBACK_IMAGE_PATH;
        } else {
            imagePath = mistakeImagePath;
        }
        this.mistakeImage = ImageUtil.loadImageResource(getClass(), imagePath);
    }

    public static boolean isRoomDeath(ToaMistake mistake) {
        return ROOM_DEATHS.contains(mistake);
    }

    /**
     * Get the grouped mistake for the specified detected mistake.
     *
     * @param mistake The detected mistake
     * @return The grouped mistake
     */
    public static ToaMistake toGroupedMistake(ToaMistake mistake) {
        if (APMEKEN_SIGHT_MISTAKES.contains(mistake)) {
            return APMEKEN_PUZZLE_SIGHT;
        } else if (WARDENS_P2_OBELISK_MISTAKES.contains(mistake)) {
            return WARDENS_P2_OBELISK;
        } else {
            return mistake;
        }
    }

    /**
     * Retrieve the chat message for the given mistake, also considering special cases and the current
     * mistake count of this mistake in the raid for the current raider, *previous* to this mistake happening.
     *
     * @param mistake      The mistake
     * @param mistakeCount The current mistake count of this mistake for the current raider in this current raid,
     *                     previously before this.
     * @return The mistake chat message to use for the raider
     */
    public static String getChatMessageForMistakeCount(ToaMistake mistake, int mistakeCount) {
        // Special case a few mistake chat messages
        if (mistake == AKKHA_UNSTABLE_ORB) {
            return getChatMessageForAkkhaUnstableOrb(mistake.getChatMessage(), mistakeCount);
        } else if (mistake == BABA_SLAM) {
            return getChatMessageForBabaSlam(mistake.getChatMessage(), mistakeCount);
        }

        return mistake.getChatMessage();
    }

    private static String getChatMessageForAkkhaUnstableOrb(String message, int mistakeCount) {
        // Keep adding ?s to the text for every subsequent mistake in this raid. A bit hacky but it's funny.
        StringBuilder sb = new StringBuilder(message);
        for (int i = 0; i < Math.min(mistakeCount, MAX_AKKHA_ORBS_CHAT_MESSAGE_LENGTH); i++) {
            sb.append(message);
        }
        return sb.toString();
    }

    private static String getChatMessageForBabaSlam(String message, int mistakeCount) {
        if (mistakeCount % 2 == 0) {
            return message;
        } else {
            return ALTERNATE_BABA_SLAM_CHAT_MESSAGE;
        }
    }
}
