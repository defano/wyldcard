package com.defano.hypertalk.ast.model;

import java.util.Arrays;

public enum VisualEffectDirection {
    OPEN,
    CLOSE,
    UP,
    DOWN,
    LEFT,
    RIGHT,
    TOP,
    CENTER,
    BOTTOM,
    IN,
    OUT;

    public static VisualEffectDirection fromHypertalkName(String hypertalkName) {
       return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(hypertalkName))
                .findFirst()
                .orElse(null);
    }
}