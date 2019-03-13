package com.defano.wyldcard.importer.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum  StackFlag {
    CANT_MODIFY(0x80),
    CANT_DELETE(0x40),
    PRIVATE_ACCESS(0x20),
    CANT_ABORT(0x08),
    CANT_PEEK(0x04);

    private final int mask;

    StackFlag(int mask) {
        this.mask = mask;
    }

    public static List<StackFlag> fromBitmask(short mask) {
        return Arrays.stream(values()).filter(sf -> (sf.mask & mask) > 0).collect(Collectors.toList());
    }
}