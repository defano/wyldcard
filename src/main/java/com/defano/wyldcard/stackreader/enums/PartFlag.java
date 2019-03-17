package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum PartFlag {
    HIDDEN(0x80),
    DONT_WRAP(0x20),
    DONT_SEARCH(0x10),
    SHARED_TEXT(0x08),
    VARIABLE_LINE_HEIGHT(0x04),
    AUTO_TAB(0x02),
    DISABLED(0x01),
    LOCK_TEXT(0x01);

    private final int mask;

    PartFlag(int mask) {
        this.mask = mask;
    }

    public static PartFlag[] fromBitmask(byte mask) {
        return Arrays.stream(values()).filter(pf -> (pf.mask & mask) > 0).toArray(PartFlag[]::new);
    }
}
