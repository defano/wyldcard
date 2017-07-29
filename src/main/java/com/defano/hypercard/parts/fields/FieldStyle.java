/*
 * FieldStyle
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.fields;

/**
 * An enumeration of field styles.
 */
public enum FieldStyle {
    TRANSPARENT("Transparent"),
    OPAQUE("Opaque"),
    SHADOW("Shadow"),
    RECTANGLE("Rectangle");

    private final String name;

    FieldStyle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FieldStyle fromName(String name) {
        for (FieldStyle thisStyle : values()) {
            if (thisStyle.getName().equalsIgnoreCase(name)) {
                return thisStyle;
            }
        }

        throw new IllegalArgumentException("No such field style: " + name);
    }

}
