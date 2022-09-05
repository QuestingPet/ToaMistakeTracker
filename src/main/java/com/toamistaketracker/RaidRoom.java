package com.toamistaketracker;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RaidRoom {

    // limbo 13392
    // in-raid lobby 14160?

    RAID_LOBBY_OUTSIDE(new int[]{13454}),
    RAID_LOBBY_INSIDE(new int[]{14160}),
    HET_PUZZLE(new int[]{14674}),
    CRONDIS_PUZZLE(new int[]{15698}),
    SCABARAS_PUZZLE(new int[]{14162}),
    APMEKEN_PUZZLE(new int[]{15186}),
    AKKHA(new int[]{14676}),
    ZEBAK(new int[]{15700}),
    KEPHRI(new int[]{14164}),
    BABA(new int[]{15188}),
    WARDENS(new int[]{15184, 15696}),
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
