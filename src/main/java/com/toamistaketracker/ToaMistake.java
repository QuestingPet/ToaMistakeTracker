package com.toamistaketracker;

import lombok.Getter;
import lombok.NonNull;

public enum ToaMistake {
    // All death chat messages will be handled by the corresponding specific DEATH enum
    DEATH("Death", "death.png", ""),
    HET_PUZZLE_LIGHT("Light", "", "Light hit me"),
    HET_PUZZLE_DARK_ORB("Dark Orb", "", "Dark hit me"),
    AKKHA_QUADRANT_SPECIAL("Akkha quad spec", "", "akkha quad spec hit me"),
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
