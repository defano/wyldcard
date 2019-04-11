package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum ExtendedPartFlag {

    SHOW_NAME(0x80),
    AUTO_SELECT(0x80),
    HILITE(0x40),
    SHOW_LINES(0x40),
    AUTO_HILITE(0x20),
    WIDE_MARGINS(0x20),
    NO_SHARING_HILITE(0x10),
    MULTIPLE_LINES(0x10);

    private final int mask;

    ExtendedPartFlag(int mask) {
        this.mask = mask;
    }

    public static ExtendedPartFlag[] fromBitmask(byte mask) {
        return Arrays.stream(values())
                .filter(epf -> (epf.mask & mask) > 0)
                .toArray(ExtendedPartFlag[]::new);
    }
}