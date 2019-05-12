package com.defano.hypertalk.ast.model.effect;

import com.defano.hypertalk.exception.HtSemanticException;

import java.util.Arrays;
import java.util.List;

public enum VisualEffectSpeed {
    VERY_FAST(250, "veryfast"),
    FAST(800, "fast"),
    SLOW(1500, "slow", "slowly"),
    VERY_SLOW(3500, "veryslow", "veryslowly");

    private final List<String> hypertalkNames;
    public final int durationMs;

    VisualEffectSpeed(int durationMs, String... hypertalkNames) {
        this.hypertalkNames = Arrays.asList(hypertalkNames);
        this.durationMs = durationMs;
    }

    public static VisualEffectSpeed fromHypertalkName(String hypertalkName) throws HtSemanticException {
        String name = hypertalkName.toLowerCase().replaceAll("\\s+", "");

        return Arrays.stream(values())
                .filter(v -> v.hypertalkNames.contains(name))
                .findFirst()
                .orElseThrow(() -> new HtSemanticException("Not a visual effect speed."));
    }
}
