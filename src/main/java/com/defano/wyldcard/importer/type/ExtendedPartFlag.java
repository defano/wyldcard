package com.defano.wyldcard.importer.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ExtendedPartFlag {

    SHOW_NAME(0x80),
    AUTO_SELECT(0x80),
    HILITE(0x40),
    SHOW_LINES(0x40),
    AUTO_HILITE(0x20),
    WIDE_MARGINS(0x20),
    NO_SHARING_HILITE(0x1),
    MULTIPLE_LINES(0x10);

    private final int mask;

    ExtendedPartFlag(int mask) {
        this.mask = mask;
    }

    public static List<ExtendedPartFlag> fromBitmask(byte mask) {
        return Arrays.stream(values()).filter(epf -> (epf.mask & mask) > 0).collect(Collectors.toList());
    }
}