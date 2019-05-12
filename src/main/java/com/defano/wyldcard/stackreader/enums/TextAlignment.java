package com.defano.wyldcard.stackreader.enums;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;

public enum TextAlignment {
    LEFT((short) 0, (short) -2),
    CENTER((short) 1),
    RIGHT((short) -1);

    private final ArrayList<Short> alignmentId;

    TextAlignment(Short... alignmentId) {
        this.alignmentId = Lists.newArrayList(alignmentId);
    }

    public static TextAlignment fromAlignmentId(short alignmentId) {
        return Arrays.stream(values())
                .filter(t -> t.alignmentId.contains(alignmentId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No such alignment: " + alignmentId));
//                .orElse(null);
    }
}
