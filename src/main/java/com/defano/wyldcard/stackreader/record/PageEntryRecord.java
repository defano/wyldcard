package com.defano.wyldcard.stackreader.record;

import com.defano.wyldcard.stackreader.enums.PageFlag;
import org.apache.commons.lang.builder.ToStringBuilder;

@SuppressWarnings("unused")
public class PageEntryRecord {

    private final int cardId;
    private PageFlag[] flags;
    protected byte[] searchHash;

    public PageEntryRecord(int cardId, PageFlag[] flags, byte[] searchHash) {
        this.cardId = cardId;
        this.flags = flags;
        this.searchHash = searchHash;
    }

    public Integer getCardId() {
        return cardId;
    }

    public PageFlag[] getFlags() {
        return flags;
    }

    public byte[] getSearchHash() {
        return searchHash;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
