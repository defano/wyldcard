package com.defano.wyldcard.util;

public class NumberUtils {

    private NumberUtils() {
    }

    public static int range(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

}
