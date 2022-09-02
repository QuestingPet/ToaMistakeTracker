package com.toamistaketracker;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RaidRoom {

    HET_PUZZLE(new int[]{14674}),
    ;

    private final int[] regionIds;

    public static RaidRoom forRegionId(int region) {
        for (RaidRoom r : RaidRoom.values()) {
            for (int regionId : r.regionIds) {
                if (regionId == region) {
                    return r;
                }
            }
        }

        return null;
    }
}
