package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum CardFlag {
    CANT_DELETE(0x1 << 14),
    HIDE_PICTURE(0x1 << 13),
    DONT_SEARCH(0x1 << 11);

    private final int mask;

    CardFlag(int mask) {
        this.mask = mask;
    }

    public static CardFlag[] fromBitmask(short mask) {
        return Arrays.stream(values()).filter(cf -> (cf.mask & mask) > 0).toArray(CardFlag[]::new);
    }
}
