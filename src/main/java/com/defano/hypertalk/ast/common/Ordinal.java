package com.defano.hypertalk.ast.common;

public enum Ordinal {
    FIRST(1), SECOND(2), THIRD(3), FOURTH(4), FIFTH(5), SIXTH(6), SEVENTH(7), 
    EIGHTH(8), NINTH(9), TENTH(10),
    
    // MAX_VALUE - 2 is required to support "after the last char of..."
    LAST(Integer.MAX_VALUE - 2),
    
    // Any negative value is interpreter to mean middle
    MIDDLE(-1);
    
    private final int value;
    
    Ordinal(int v) {
        value = v;
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
}
