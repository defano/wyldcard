/*
 * ButtonStyle
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.buttons;

/**
 * An enumeration of button styles available to the user.
 */
public enum ButtonStyle {

    DEFAULT("Default"),
    CLASSIC("Classic"),
    CHECKBOX("Checkbox"),
    RADIO("Radio"),
    MENU("Menu"),
    RECTANGULAR("Rectangular"),
    OVAL("Oval"),
    TRANSPARENT("Transparent"),
    OPAQUE("Opaque"),
    SHADOW("Shadow");

    private final String name;

    ButtonStyle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ButtonStyle fromName(String name) {
        for (ButtonStyle thisStyle : values()) {
            if (thisStyle.getName().equalsIgnoreCase(name)) {
                return thisStyle;
            }
        }

        throw new IllegalArgumentException("No such button style: " + name);
    }
}
