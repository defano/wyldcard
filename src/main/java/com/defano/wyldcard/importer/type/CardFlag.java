package com.defano.wyldcard.importer.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CardFlag {
    CANT_DELETE(0x1 << 14),
    HIDE_PICTURE(0x1 << 13),
    DONT_SEARCH(0x1 << 11);

    private final int mask;

    CardFlag(int mask) {
        this.mask = mask;
    }

    public static List<CardFlag> fromBitmask(short mask) {
        return Arrays.stream(values()).filter(cf -> (cf.mask & mask) > 0).collect(Collectors.toList());
    }
}
