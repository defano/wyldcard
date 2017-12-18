package com.defano.hypertalk.ast.model;

public enum DateLength {
    LONG,
    SHORT,
    ABBREVIATED,
    DEFAULT;

    public BuiltInFunction getTimeFunction() {
        switch (this) {
            case LONG:
                return BuiltInFunction.LONG_TIME;
            case DEFAULT:
            case SHORT:
                return BuiltInFunction.SHORT_TIME;
            case ABBREVIATED:
                return BuiltInFunction.ABBREV_TIME;

            default:
                throw new IllegalArgumentException("Bug! Unimplemented format.");
        }
    }

    public BuiltInFunction getDateFunction() {
        switch (this) {
            case LONG:
                return BuiltInFunction.LONG_DATE;
            case DEFAULT:
            case SHORT:
                return BuiltInFunction.SHORT_DATE;
            case ABBREVIATED:
                return BuiltInFunction.ABBREV_DATE;

            default:
                throw new IllegalArgumentException("Bug! Unimplemented format.");
        }
    }

}
