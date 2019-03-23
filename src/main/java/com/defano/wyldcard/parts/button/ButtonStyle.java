package com.defano.wyldcard.parts.button;

/**
 * An enumeration of button styles.
 */
public enum ButtonStyle {

    NATIVE("Native"),
    CLASSIC("Classic"),
    DEFAULT("Default"),
    ROUND_RECT("Round Rect"),
    CHECKBOX("Checkbox"),
    RADIO("Radio"),
    POPUP("Popup"),
    RECTANGULAR("Rectangle"),
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

    @Override
    public String toString() {
        return name;
    }

    public static ButtonStyle fromName(String name) {
        for (ButtonStyle thisStyle : values()) {
            if (thisStyle.getName().equalsIgnoreCase(name)) {
                return thisStyle;
            }
        }

        return NATIVE;
    }
}
