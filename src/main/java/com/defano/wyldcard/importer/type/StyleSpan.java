package com.defano.wyldcard.importer.type;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class StyleSpan {

    private final short textPosition;
    private final short styleId;

    public StyleSpan(short textPosition, short styleId) {
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
