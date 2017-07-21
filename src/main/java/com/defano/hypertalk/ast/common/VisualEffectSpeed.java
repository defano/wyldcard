package com.defano.hypertalk.ast.common;

public enum VisualEffectSpeed {
    VERY_FAST(500),
    FAST(1000),
    SLOW(2000),
    VERY_SLOW(3500);

    public final int durationMs;

    VisualEffectSpeed(int durationMs) {
        this.durationMs = durationMs;
    }
}
