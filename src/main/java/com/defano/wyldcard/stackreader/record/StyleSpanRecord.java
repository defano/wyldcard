package com.defano.wyldcard.stackreader.record;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SuppressWarnings("unused")
public class StyleSpanRecord {

    private final short textPosition;
    private final short styleId;

    public StyleSpanRecord(short textPosition, short styleId) {
        this.textPosition = textPosition;
        this.styleId = styleId;
    }

    public short getTextPosition() {
        return textPosition;
    }

    public short getStyleId() {
        return styleId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
