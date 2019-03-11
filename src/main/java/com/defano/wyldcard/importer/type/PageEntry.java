package com.defano.wyldcard.importer.type;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PageEntry {

    private final int pageId;
    private final short pageEntryCount;

    public PageEntry(int pageId, short pageEntryCount) {
        this.pageId = pageId;
        this.pageEntryCount = pageEntryCount;
    }

    public int getPageId() {
        return pageId;
    }

    public short getPageEntryCount() {
        return pageEntryCount;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
