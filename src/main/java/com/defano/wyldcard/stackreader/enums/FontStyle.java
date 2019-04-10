package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum FontStyle {
    GROUP(0x80),
    EXTEND(0x40),
    CONDENSE(0x20),
    SHADOW(0x10),
    OUTLINE(0x08),
    UNDERLINE(0x04),
    ITALIC(0x02),
    BOLD(0x01);

    private final int mask;

    FontStyle(int mask) {
        this.mask = mask;
    }

    public static FontStyle[] fromBitmask(byte mask) {
        if (mask == -1) {
            return null;
        }

        return Arrays.stream(values()).filter(ts -> (ts.mask & mask) > 0).toArray(FontStyle[]::new);
    }
}
