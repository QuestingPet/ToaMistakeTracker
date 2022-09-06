package com.toamistaketracker;

import lombok.Getter;
import lombok.NonNull;

public enum ToaMistake {
    // All death chat messages will be handled by the corresponding specific DEATH enum
    DEATH("Death", "death.png", ""),
    HET_PUZZLE_LIGHT("Light", "", "Ah! It burns!"),
    HET_PUZZLE_DARK_ORB("Dark Orb", "", "I can't dodge!"),
    AKKHA_SPECIAL_QUADRANT_BOMB("Akkha quad spec", "", "I'm too slow!"),
    AKKHA_SPECIAL_ELEMENTAL_ORBS("Akkha spec elemental orbs", "", "Weeeee!"),
    AKKHA_UNSTABLE_ORB("Akkha unstable orb", "", "Akkha unstable orb"),
    ;

    @Getter
    @NonNull
    private final String mistakeName;

    @Getter
    @NonNull
    // TODO: Maybe have a list of messages to say after reaching a certain amount of mistakes
    // TODO: Maybe have these be configurable for each mistake in the config?
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
