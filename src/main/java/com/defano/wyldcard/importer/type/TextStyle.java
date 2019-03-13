package com.defano.wyldcard.importer.type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// it 7: group, Bit 6: extend, Bit 5: condense, Bit 4: shadow, Bit 3: outline, Bit 2: underline, Bit 1: italic, Bit 0: bold
public enum TextStyle {
    GROUP(0x80),
    EXTEND(0x40),
    CONDENSE(0x20),
    SHADOW(0x10),
    OUTLINE(0x08),
    UNDERLINE(0x04),
    ITALIC(0x02),
    BOLD(0x01);

    private final int mask;

    TextStyle(int mask) {
        this.mask = mask;
    }

    public static List<TextStyle> fromBitmask(byte mask) {
        return Arrays.stream(values()).filter(ts -> (ts.mask & mask) > 0).collect(Collectors.toList());
    }
}
