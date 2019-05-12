package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum PageFlag {
    MARKED_CARD(0x01 << 4),
    HAS_TEXT(0x1 << 5),
    START_OF_BACKGROUND(0x01 << 6),
    HAS_NAME(0x01 << 7);

    private final int mask;

    PageFlag(int mask) {
        this.mask = mask;
    }

    public static PageFlag[] fromBitmask(byte mask) {
        return Arrays.stream(values()).filter(ts -> (ts.mask & mask) > 0).toArray(PageFlag[]::new);
    }
}
