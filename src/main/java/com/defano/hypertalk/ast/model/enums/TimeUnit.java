package com.defano.hypertalk.ast.model.enums;

public enum TimeUnit {
    TICKS, SECONDS;

    public int toMilliseconds (double count) {
        switch (this) {
            case SECONDS:
                return (int)(count * 1000);
            case TICKS:
                return (int)((count / 60.0) * 1000);
            default:
                throw new RuntimeException("Bug! Unimplemented case.");
        }
    }
}
