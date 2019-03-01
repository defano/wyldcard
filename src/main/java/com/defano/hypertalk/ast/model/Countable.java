package com.defano.hypertalk.ast.model;

public enum Countable {

    CARDS, CARDS_OF, MARKED_CARDS, CHARS_OF, ITEMS_OF, WORDS_OF, LINES_OF, CARD_BUTTONS, BACKGROUNDS, CARD_FIELDS,
    CARD_PARTS, BKGND_BUTTONS, BKGND_FIELDS, BKGND_PARTS, WINDOWS, MENUS, MENU_ITEMS, MARKED_CARDS_OF;

    public boolean isCardPart() {
        return this == CARD_PARTS || this == CARD_BUTTONS || this == CARD_FIELDS;
    }

    public boolean isBkgndPart() {
        return this == BKGND_PARTS || this == BKGND_BUTTONS || this == BKGND_FIELDS;
    }
}
