package com.toamistaketracker;

import lombok.Getter;
import lombok.NonNull;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public enum ToaMistake {
    // Deaths
    DEATH("Death", "", "death.png"), // Chat message handled in deaths below
    DEATH_HET("Path of Het Death", "I'm planking!", "death.png"),
    DEATH_CRONDIS("Path of Crondis Death", "I'm planking!", "death.png"),
    DEATH_SCABARAS("Path of Scabaras Death", "I'm planking!", "death.png"),
    DEATH_APMEKEN("Path of Apmeken Death", "I'm planking!", "death.png"),
    DEATH_WARDENS("Wardens Death", "I'm planking!", "death.png"),

    // Het
    HET_PUZZLE_LIGHT("Het Puzzle Energy Beam", "Ah! It burns!", "death.png"),
    HET_PUZZLE_DARK_ORB("Het Puzzle Dark Orb", "Embrace Darkness!", "death.png"),
    AKKHA_SPECIAL_QUADRANT_BOMB("Akkha Quadrant Bombs", "I'm too slow!", "death.png"),
    AKKHA_SPECIAL_ELEMENTAL_ORBS("Akkha Elemental Orbs", "I'm griefing!", "death.png"),
    AKKHA_UNSTABLE_ORB("Akkha Unstable Orb", "???", "death.png"), // TODO: Increase lol

    // Crondis
    CRONDIS_PUZZLE_LOW_WATER("Path of Crondis Watering", "This jug feels a little light...", "death.png"),
    ZEBAK_ACID_TILE("Zebak Acid Tile", "I'm drowning in acid!", "death.png"),
    ZEBAK_BLOOD_CLOUD("Zebak Blood Cloud", "I'm on a blood cloud!", "death.png"),
    ZEBAK_EARTHQUAKE("Zebak Earthquake", "Nihil!", "death.png"),
    ZEBAK_WAVE("Zebak Wave", "I'm surfing!", "death.png"),

    // Scabaras
    KEPHRI_BOMB("Kephri Bomb", "I'm exploding!", "death.png"),
    KEPHRI_SWARM_HEAL("Kephri Swarm Heal", "The swarms are going in!", "death.png"),
    KEPHRI_EGG_EXPLODE("Kephri Egg Explode", "I've been hatched!", "death.png"),

    // Apmeken
    APMEKEN_PUZZLE_SIGHT("Apmeken Sight", "", "death.png"),
    APMEKEN_PUZZLE_VENT("Apmeken Vent", "I'm fuming!", ""),
    APMEKEN_PUZZLE_PILLAR("Apmeken Pillar", "The sky is falling!", ""),
    APMEKEN_PUZZLE_CORRUPTION("Apmeken Corruption", "I've been corrupted!", ""),
    APMEKEN_PUZZLE_VENOM("Apmeken Venom Tile", "It's venomous!", "death.png"),
    APMEKEN_PUZZLE_VOLATILE("Apmeken Volatile", "I'm exploding!", "death.png"),
    BABA_SLAM("Ba-Ba Slam", "Ahh there's not even an animation!", "death.png"),
    BABA_PROJECTILE_BOULDER("Ba-Ba Projectile Boulder", "I got rocked!", "death.png"),
    BABA_ROLLING_BOULDER("Ba-Ba Rolling Boulder", "They see me rollin!", "death.png"),
    BABA_FALLING_BOULDER("Ba-Ba Falling Boulder", "It's raining!", "death.png"),
    BABA_BANANA("Ba-Ba Banana", "Who put that there?", "death.png"),
    BABA_GAP("Ba-Ba Gap", "I'm going down!", "death.png"),

    // Wardens
    WARDENS_P1_PYRAMID("Wardens P1 Pyramid", "I'm disco-ing!", "death.png"),
    WARDENS_P2_OBELISK("Wardens P2 Obelisk", "", "death.png"),
    WARDENS_P2_DDR("Wardens P2 DDR", "I'm dancing!", ""),
    WARDENS_P2_WINDMILL("Wardens P2 Windmill", "I'm winded!", ""),
    WARDENS_P2_BOMBS("Wardens P2 Bombs", "I'm getting bombed!", ""),
    WARDENS_P2_BIND("Wardens P2 Bind", "I'm in jail!", "death.png"),
    WARDENS_P2_SPECIAL_PRAYER("Wardens P2 Special Prayer", "What even was that attack?", "death.png"),
    WARDENS_P3_EARTHQUAKE("Wardens P3 Earthquake", "I'm tripping!", "death.png"),
    WARDENS_P3_KEPHRI("Wardens P3 Kephri", "I'm exploding!", "death.png"),
    WARDENS_P3_BABA("Wardens P3 Ba-Ba", "It's raining!", "death.png"),
    WARDENS_P3_LIGHTNING("Wardens P3 Lightning", "It's electric!", "death.png"),
    ;

    private static final Set<ToaMistake> ROOM_DEATHS = EnumSet.of(
            DEATH_HET, DEATH_CRONDIS, DEATH_SCABARAS, DEATH_APMEKEN, DEATH_WARDENS);

    private static final Set<ToaMistake> APMEKEN_SIGHT_MISTAKES = EnumSet.of(
            APMEKEN_PUZZLE_VENT, APMEKEN_PUZZLE_PILLAR, APMEKEN_PUZZLE_CORRUPTION);
    private static final Set<ToaMistake> WARDENS_P2_OBELISK_MISTAKES = EnumSet.of(
            WARDENS_P2_DDR, WARDENS_P2_WINDMILL, WARDENS_P2_BOMBS);

    private static final String FALLBACK_IMAGE_PATH = "death.png";

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
            imagePath = "death.png";
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
}
