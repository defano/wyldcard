package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum ReportRecordFlag {

    CHANGE_HEIGHT(0x01 << 13),
    CHANGE_STYLE(0x01 << 12),
    CHANGE_SIZE(0x01 << 11),
    CHANGE_FONT(0x01 << 10),
    INVERT(0x01 << 4),
    RIGHT_FRAME(0x01 << 3),
    BOTTOM_FRAME(0x01 << 2),
    LEFT_FRAME(0x01 << 1),
    TOP_FRAME(0x01);

    private final int mask;

    ReportRecordFlag (int mask) {
        this.mask = mask;
    }

    public static ReportRecordFlag[] fromBitmask(short mask) {
        return Arrays.stream(values()).filter(sf -> (sf.mask & mask) > 0).toArray(ReportRecordFlag[]::new);
    }
}
