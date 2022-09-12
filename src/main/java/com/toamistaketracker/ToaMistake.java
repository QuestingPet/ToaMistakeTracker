package com.toamistaketracker;

import lombok.Getter;
import lombok.NonNull;

public enum ToaMistake {
    DEATH("Death", "death.png", "I'm planking!"),
    HET_PUZZLE_LIGHT("Light", "", "Ah! It burns!"),
    HET_PUZZLE_DARK_ORB("Dark Orb", "", "I can't dodge!"),
    AKKHA_SPECIAL_QUADRANT_BOMB("Akkha quad spec", "", "I'm too slow!"),
    AKKHA_SPECIAL_ELEMENTAL_ORBS("Akkha spec elemental orbs", "", "I'm griefing!"),
    AKKHA_UNSTABLE_ORB("Akkha unstable orb", "", "???"),
    CRONDIS_PUZZLE_LOW_WATER("Path of Crondis Low Watering", "", "I lost some water!"), // This jug feels a little light (?)
    ZEBAK_ACID_TILE("Zebak acid tile", "", "I'm drowning in acid!"),
    ZEBAK_BLOOD_HEAL("Zebak blood heal", "", "I'm on a blood cloud!"),
    ZEBAK_EARTHQUAKE("Zebak earthquake", "", "I'm moving!"),
    ZEBAK_WAVE("Zebak wave", "", "I'm surfing!"),
    KEPHRI_BOMB("Kephri bomb","","I'm exploding!"),
    KEPHRI_SWARM_HEAL("Kephri swarm heal","","The swarms are going in!"),
    KEPHRI_EGG_EXPLODE("Kephri egg explode","","I've been hatched!"),
    APMEKEN_PUZZLE_VENT("Apmeken vent", "", "I'm fuming!"),
    APMEKEN_PUZZLE_PILLAR("Apmeken pillar", "", "The sky is falling!"),
    APMEKEN_PUZZLE_CORRUPTION("Apmeken corruption", "", "I've been corrupted!"),
    APMEKEN_PUZZLE_VENOM("Apmeken venom", "", "It's venomous!"),
    APMEKEN_PUZZLE_VOLATILE("Apmeken volatile", "", "I'm exploding!"),
    BABA_SLAM("Ba-Ba slam", "", "Ahh there's not even an animation!"),
    BABA_PROJECTILE_BOULDER ("Ba-Ba projectile boulder", "", "I got rocked!"),
    BABA_ROLLING_BOULDER ("Ba-Ba rolling boulder", "", "They see me rollin!"),
    BABA_GAP("Ba-Ba gap", "", "I'm going down!"),
    BABA_BANANA("Ba-Ba banana", "", "I'm slipping!"),
    BABA_FALLING_BOULDER("Ba-Ba falling boulder", "", "It's raining!"),
    WARDENS_P1_PYRAMID("Wardens P1 pyramid", "", "I'm disco-ing!"),
    WARDENS_P2_DDR("Wardens P2 DDR", "", "I'm dancing!"),
    WARDENS_P2_WINDMILL("Wardens P2 windmill", "", "I'm winded!"),
    WARDENS_P2_BOMBS("Wardens P2 bombs", "", "I'm getting bombed!"),
    WARDENS_P2_BIND("Wardens P2 bind", "", "I'm in jail!"),
    WARDENS_P2_SPECIAL_PRAYER("Wardens P2 special prayer", "", "What even was that attack?"),
    WARDENS_P3_EARTHQUAKE("Wardens P3 earthquake", "", "I'm tripping!"),
    WARDENS_P3_KEPHRI("Wardens P3 kephri", "", "I'm exploding!"),
    WARDENS_P3_LIGHTNING("Wardens P3 lightning", "", "It's electric!"),
    ;

    @Getter
    @NonNull
    private final String mistakeName;

    @Getter
    @NonNull
    private final String chatMessage;

//    @Getter
//    @NonNull
//    private final BufferedImage mistakeImage;

    ToaMistake(@NonNull String mistakeName, @NonNull String mistakeImagePath, @NonNull String chatMessage) {
        this.mistakeName = mistakeName;
        this.chatMessage = chatMessage;

//        this.mistakeImage = ImageUtil.loadImageResource(getClass(), mistakeImagePath);
    }
}
