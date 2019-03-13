package com.defano.wyldcard.importer.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<PartFlag> fromBitmask(byte mask) {
        return Arrays.stream(values()).filter(pf -> (pf.mask & mask) > 0).collect(Collectors.toList());
    }
}
