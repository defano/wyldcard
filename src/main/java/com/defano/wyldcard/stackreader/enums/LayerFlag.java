package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum LayerFlag {
    CANT_DELETE(0x1 << 14),
    HIDE_PICTURE(0x1 << 13),
    DONT_SEARCH(0x1 << 11);

    private final int mask;

    LayerFlag(int mask) {
        this.mask = mask;
    }

    public static LayerFlag[] fromBitmask(short mask) {
        return Arrays.stream(values()).filter(cf -> (cf.mask & mask) > 0).toArray(LayerFlag[]::new);
    }
}
