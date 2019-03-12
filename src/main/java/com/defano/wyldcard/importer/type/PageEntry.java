package com.defano.wyldcard.importer.type;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PageEntry {

    private final int pageId;
    private final boolean isMarked;

    public PageEntry(int pageId, boolean isMarked) {
        this.pageId = pageId;
        this.isMarked = isMarked;
    }

    public int getPageId() {
        return pageId;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
