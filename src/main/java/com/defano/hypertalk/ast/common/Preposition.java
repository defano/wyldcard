/*
 * Preposition
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * Preposition.java
 * @author matt.defano@gmail.com
 * 
 * Enumeration of acceptable prepositions
 */

package com.defano.hypertalk.ast.common;

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
