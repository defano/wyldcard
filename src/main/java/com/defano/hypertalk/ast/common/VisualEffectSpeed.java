package com.defano.hypertalk.ast.common;

public enum VisualEffectSpeed {
    VERY_FAST(250),
    FAST(800),
    SLOW(1500),
    VERY_SLOW(3500);

    public final int durationMs;

    VisualEffectSpeed(int durationMs) {
        this.durationMs = durationMs;
    }
}
