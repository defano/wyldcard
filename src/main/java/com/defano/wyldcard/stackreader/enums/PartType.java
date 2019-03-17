package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum PartType {
    BUTTON(1),
    FIELD(2);

    private final short value;

    PartType(int value) {
        this.value = (short) value;
    }

    public static PartType fromTypeId(byte typeId) {
        return Arrays.stream(values()).filter(t -> t.value == typeId).findFirst().orElse(null);
    }
}
