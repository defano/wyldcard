package hypercard.parts.buttons;

public enum ButtonStyle {
    DEFAULT("Default"),
    CHECKBOX("Checkbox"),
    RADIO("Radio"),
    MENU("Menu"),
    RECTANGULAR("Rectangular"),
    TRANSPARENT("Transparent");

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
