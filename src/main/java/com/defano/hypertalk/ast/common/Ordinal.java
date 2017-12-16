package com.defano.hypertalk.ast.common;

public enum Ordinal {
    FIRST(1, "first"),
    SECOND(2, "second"),
    THIRD(3, "third"),
    FOURTH(4, "fourth"),
    FIFTH(5, "fifth"),
    SIXTH(6, "sixth"),
    SEVENTH(7, "seventh"),
    EIGHTH(8, "eighth"),
    NINTH(9, "ninth"),
    TENTH(10, "tenth"),

    // MAX_VALUE - 2 is required to support "after the last char of..."
    LAST(Integer.MAX_VALUE - 2, "last"),
    
    // -1 interpreted to mean middle
    MIDDLE(-1, "mid", "middle"),

    // -2 interpreted to mean any
    ANY(-2, "any");

    private final int value;
    private final String[] hyperTalkIdentifiers;
    
    Ordinal(int v, String... hyperTalkIdentifiers) {
        this.value = v;
        this.hyperTalkIdentifiers = hyperTalkIdentifiers;
    }

    public static Ordinal fromHyperTalkIdentifier(String identifier) {
        for (Ordinal thisOrdinal : values()) {
            for (String thisIdentifier : thisOrdinal.hyperTalkIdentifiers) {
                if (thisIdentifier.equalsIgnoreCase(identifier)) {
                    return thisOrdinal;
                }
            }
        }

        throw new IllegalArgumentException("No such ordinal: " + identifier);
    }

    public String stringValue() {
        return value().toString();
    }
    
    public Value value() {
        return new Value(value);
    }
    
    public int intValue () {
        return value;
    }

    public static boolean reservedValue(int value) {
        return value == ANY.intValue() || value == MIDDLE.intValue() || value == LAST.intValue();
    }
}
