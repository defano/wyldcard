package com.defano.hypertalk.ast.model;

public enum Preposition {
    BEFORE, AFTER, INTO;

    public static Preposition fromString(String s) {
        switch (s) {
            case "before": return BEFORE;
            case "after": return AFTER;
            case "into": return INTO;
            default: throw new IllegalArgumentException("Bug! Unimplemented preposition.");
        }
    }
}
