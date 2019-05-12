package com.defano.wyldcard.stackreader.record;

import com.defano.wyldcard.stackreader.block.Block;
import com.defano.wyldcard.stackreader.enums.FontStyle;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.IOException;

@SuppressWarnings("unused")
public class StyleRecord {

    private int styleId;
    private short lineHeight;
    private short fontAscent;
    private short fontId;
    private FontStyle[] styles;
    private short fontSize;
    private short red;
    private short green;
    private short blue;

    public int getStyleId() {
        return styleId;
    }

    public short getLineHeight() {
        return lineHeight;
    }

    public short getFontAscent() {
        return fontAscent;
    }

    public short getFontId() {
        return fontId;
    }

    public FontStyle[] getStyles() {
        return styles;
    }

    public short getFontSize() {
        return fontSize;
    }

    public short getRed() {
        return red;
    }

    public short getGreen() {
        return green;
    }

    public short getBlue() {
        return blue;
    }

    public static StyleRecord deserialize(Block parent, byte[] data) throws ImportException {
        StyleRecord style = new StyleRecord();
        StackInputStream sis = new StackInputStream(data);

        try {
            style.styleId = sis.readInt();
            sis.readBytes(4);
            style.lineHeight = sis.readShort();
            style.fontAscent = sis.readShort();
            style.fontId = sis.readShort();
            style.styles = FontStyle.fromBitmask((byte) ((sis.readShort() & 0xff00) >> 8));
            style.fontSize = sis.readShort();
            style.red = sis.readShort();
            style.green = sis.readShort();
            style.blue = sis.readShort();
        } catch (IOException e) {
            throw new ImportException(parent, "Malformed style record.");
        }

        return style;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
