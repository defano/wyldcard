package hypercard.parts.fields;

public enum FieldStyle {
    TRANSPARENT("Transparent"),
    OPAQUE("Opaque");

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
