package com.defano.wyldcard.stackreader.record;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@SuppressWarnings("unused")
public class FontRecord {

    private final int fontId;
    private final String fontName;

    public FontRecord(int fontId, String fontName) {
        this.fontId = fontId;
        this.fontName = fontName;
    }

    public int getFontId() {
        return fontId;
    }

    public String getFontName() {
        return fontName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
