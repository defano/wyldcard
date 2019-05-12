package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum  ReportFlag {
    LEFT_TO_RIGHT(0x0100),
    DYNAMIC_HEIGHT(0x0001);

    private final int mask;

    ReportFlag(int mask) {
        this.mask = mask;
    }

    public static ReportFlag[] fromBitmask(short mask) {
        return Arrays.stream(values()).filter(pf -> (pf.mask & mask) > 0).toArray(ReportFlag[]::new);
    }
}
