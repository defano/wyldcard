package com.defano.wyldcard.importer.type;

import java.util.Arrays;

public enum TextAlignment {
    LEFT((short) 0),
    CENTER((short) 1),
    RIGHT((short) -1);

    private short alignmentId;

    TextAlignment(short alignmentId) {
        this.alignmentId = alignmentId;
    }

    public static TextAlignment fromAlignmentId(short alignmentId) {
        return Arrays.stream(values()).filter(t -> t.alignmentId == alignmentId).findFirst().orElse(null);
    }
}
