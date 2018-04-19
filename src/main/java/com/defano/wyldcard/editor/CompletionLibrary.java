package com.defano.wyldcard.editor;

import javax.swing.*;

public enum CompletionLibrary {
    COMMANDS("Command", "syntax-help/commands.json", null),
    FUNCTIONS("Function", "syntax-help/functions.json", null),
    CONSTRUCTS("Language Construct", "syntax-help/constructs.json", null),
    PART_PROPERTIES("Part Property", "syntax-help/part-properties.json", null),
    BUTTON_PROPERTIES("Button Property", "syntax-help/button-properties.json", null),
    FIELD_PROPERTIES("Field Property", "syntax-help/field-properties.json", null),
    MENU_PROPERTIES("Menu Property", "syntax-help/menu-properties.json", null),
    CARD_PROPERTIES("Card/Background Property", "syntax-help/card-properties.json", null),
    GLOBAL_PROPERTIES("WyldCard Property", "syntax-help/global-properties.json", null);

    private final String iconPath;
    private final String json;
    private final String name;

    CompletionLibrary(String categoryName, String json, String iconPath) {
        this.name = categoryName;
        this.json = json;
        this.iconPath = iconPath;
    }

    public Icon getIcon() {
        try {
            return new ImageIcon(getClass().getResource(iconPath));
        } catch (Throwable e) {
            return null;
        }
    }

    public String getJson() {
        return json;
    }

    public String getName() {
        return name;
    }
}
