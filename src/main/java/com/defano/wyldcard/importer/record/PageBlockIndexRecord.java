package com.defano.wyldcard.importer.record;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PageBlockIndexRecord {

    private final int pageId;
    private final short pageEntryCount;

    public PageBlockIndexRecord(int pageId, short pageEntryCount) {
        this.pageId = pageId;
        this.pageEntryCount = pageEntryCount;
    }

    /**
     * Gets the ID of the indexed PAGE block
     * @return The id of the indexed PAGE block
     */
    public int getPageId() {
        return pageId;
    }

    /**
     * Gets the number of pages (cards) present in the indexed PAGE block.
     * @return The number of pages in the indexed block.
     */
    public short getPageEntryCount() {
        return pageEntryCount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
