package com.defano.wyldcard.stackreader.record;

import org.apache.commons.lang.builder.ToStringBuilder;

@SuppressWarnings("unused")
public class PageEntryRecord {

    private final int cardId;
    private final boolean isMarked;

    public PageEntryRecord(int cardId, boolean isMarked) {
        this.cardId = cardId;
        this.isMarked = isMarked;
    }

    public int getCardId() {
        return cardId;
    }

    public boolean isMarked() {
        return isMarked;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
