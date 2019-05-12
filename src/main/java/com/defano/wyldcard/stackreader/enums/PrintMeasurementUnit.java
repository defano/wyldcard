package com.defano.wyldcard.stackreader.enums;

import java.util.Arrays;

public enum  PrintMeasurementUnit {
    CENTIMETERS(0),
    MILLIMETERS(1),
    INCHES(2),
    POINTS(3);

    private final int value;

    PrintMeasurementUnit(int value) {
        this.value = value;
    }

    public static PrintMeasurementUnit fromByte(byte value) {
        return Arrays.stream(values()).filter(m -> m.value == value).findFirst().orElse(null);
    }
}
