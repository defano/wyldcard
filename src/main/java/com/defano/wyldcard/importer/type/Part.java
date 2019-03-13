package com.defano.wyldcard.importer.type;

import com.defano.wyldcard.importer.StackInputStream;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class Part {

    final short size; // size of the part, including this header
    short partId; // ID number of the part
    PartType partType;
    List<PartFlag> flags;
    short top; // top of the part's rectangle
    short left; // left of the part's rectangle
    short bottom; // bottom of the part's rectangle
    short right; // right of the part's rectangle
    List<ExtendedPartFlag> extendedFlags;
    int family;
    PartStyle style; // HyperCard only looks at the least significant 4 bits of this field
    short titleWidthOrLastSelectedLine;
    short iconIdOrFirstSelectedLine;
    TextAlignment textAlign;
    short textFontId; // use the FTBL block to translate this to a font name
    short textSize;
    List<TextStyle> textStyles;
    short textHeight;
    String name;
    String script; // the button or field script

    public Part(short entrySize, byte[] data) {
        StackInputStream sis = new StackInputStream(data);
        this.size = entrySize;

        try {
            partId = sis.readShort();
            partType = PartType.fromTypeId(sis.readByte());
            flags = PartFlag.fromBitmask(sis.readByte());
            top = sis.readShort();
            left = sis.readShort();
            bottom = sis.readShort();
            right = sis.readShort();

            byte extendedFlagsMask = sis.readByte();
            extendedFlags = ExtendedPartFlag.fromBitmask(extendedFlagsMask);
            family = extendedFlagsMask & 0x0f;

            style = PartStyle.ofPartStyleId(sis.readByte());
            titleWidthOrLastSelectedLine = sis.readShort();
            iconIdOrFirstSelectedLine = sis.readShort();
            textAlign = TextAlignment.fromAlignmentId(sis.readShort());
            textFontId = sis.readShort();
            textSize = sis.readShort();
            textStyles = TextStyle.fromBitmask(sis.readByte());
            sis.readByte();
            textHeight = sis.readShort();
            name = sis.readString();
            sis.readByte();
            script = sis.readString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Rectangle getPartRectangle() {
        return new Rectangle(left, top, right - left, bottom - top);
    }

    public int getTitleWidth() {
        return titleWidthOrLastSelectedLine;
    }

    public int getLastSelectedLine() {
        return titleWidthOrLastSelectedLine;
    }

    public int getFirstSelectedLine() {
        return iconIdOrFirstSelectedLine;
    }

    public int getIconId() {
        return getIconId();
    }

    public TextAlignment getTextAlign() {
        return textAlign;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
