package com.defano.hypertalk.ast.model;

public enum LengthAdjective {
    LONG("long"),
    SHORT("short"),
    ABBREVIATED("abbreviated"),
    DEFAULT("");

    public final String hypertalkIdentifier;

    LengthAdjective(String hypertalkIdentifier) {
        this.hypertalkIdentifier = hypertalkIdentifier;
    }

    public String apply(String propertyName) {
        if (this == LengthAdjective.DEFAULT) {
            return propertyName;
        } else {
            return hypertalkIdentifier + " " + propertyName;
        }
    }

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
