package com.defano.wyldcard.importer.type;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PageEntry {

    private final int cardId;
    private final boolean isMarked;

    public PageEntry(int cardId, boolean isMarked) {
        this.cardId = cardId;
        this.isMarked = isMarked;
    }

    public int getCardId() {
        return cardId;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
