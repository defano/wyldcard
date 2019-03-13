package com.defano.wyldcard.importer.type;

import java.util.Arrays;

public enum PartStyle {

    TRANSPARENT(0),
    OPAQUE(1),
    RECTANGLE(2),
    ROUND_RECTANGLE(3),
    SHADOW(4),
    CHECKBOX(5),
    RADIO(6),
    SCROLLING(7),
    STANDARD(8),
    DEFAULT(9),
    OVAL(10),
    POPUP(11);

    private int partStyleId;

    PartStyle(int partStyleId) {
        this.partStyleId = partStyleId;
    }

    public static PartStyle ofPartStyleId(byte partStyleId) {
        return Arrays.stream(values()).filter(s -> s.partStyleId == partStyleId).findFirst().orElse(null);
    }
}
