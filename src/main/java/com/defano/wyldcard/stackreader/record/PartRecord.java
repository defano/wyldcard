package com.defano.wyldcard.stackreader.record;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.block.Block;
import com.defano.wyldcard.stackreader.enums.*;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.awt.*;
import java.io.IOException;

@SuppressWarnings("unused")
public class PartRecord {

    private transient HyperCardStack stack;

    private short size; // size of the part, including this header
    private short partId; // ID number of the part
    private PartType partType;
    private PartFlag[] flags;
    private short top; // top of the part's rectangle
    private short left; // left of the part's rectangle
    private short bottom; // bottom of the part's rectangle
    private short right; // right of the part's rectangle
    private ExtendedPartFlag[] extendedFlags;
    private int family;
    private PartStyle style; // HyperCard only looks at the least significant 4 bits of this field
    private short titleWidthOrLastSelectedLine;
    private short iconIdOrFirstSelectedLine;
    private TextAlignment textAlign;
    private short textFontId; // use the FTBL block to translate this to a font name
    private short textSize;
    private FontStyle[] fontStyles;
    private short textHeight;
    private String name;
    private String script; // the button or field script

    public static PartRecord deserialize(Block parent, short entrySize, byte[] data, ImportResult report) throws ImportException {
        PartRecord part = new PartRecord();
        StackInputStream sis = new StackInputStream(data);

        part.stack = parent.getStack();
        part.size = entrySize;

        try {
            part.partId = sis.readShort();
            part.partType = PartType.fromTypeId(sis.readByte());
            part.flags = PartFlag.fromBitmask(sis.readByte());
            part.top = sis.readShort();
            part.left = sis.readShort();
            part.bottom = sis.readShort();
            part.right = sis.readShort();

            byte extendedFlagsMask = sis.readByte();
            part.extendedFlags = ExtendedPartFlag.fromBitmask(extendedFlagsMask);
            part.family = extendedFlagsMask & 0x0f;

            part.style = PartStyle.ofPartStyleId(sis.readByte());
            part.titleWidthOrLastSelectedLine = sis.readShort();
            part.iconIdOrFirstSelectedLine = sis.readShort();
            part.textAlign = TextAlignment.fromAlignmentId(sis.readShort());
            part.textFontId = sis.readShort();
            part.textSize = sis.readShort();
            part.fontStyles = FontStyle.fromBitmask(sis.readByte());
            sis.readByte();
            part.textHeight = sis.readShort();
            part.name = sis.readString();
            sis.readByte();
            part.script = sis.readString();

        } catch (IOException e) {
            report.throwError(parent, "Malformed part record; stack is corrupt.");
        }

        return part;
    }

    public Rectangle getPartRectangle() {
        return new Rectangle(left, top, right - left, bottom - top);
    }

    public int getTitleWidth() {
        if (partType == PartType.FIELD) {
            throw new IllegalStateException("This value does not apply to field parts.");
        }
        return titleWidthOrLastSelectedLine;
    }

    public int getLastSelectedLine() {
        if (partType == PartType.BUTTON) {
            throw new IllegalStateException("This value does not apply to button parts.");
        }
        return titleWidthOrLastSelectedLine;
    }

    public int getFirstSelectedLine() {
        if (partType == PartType.BUTTON) {
            throw new IllegalStateException("This value does not apply to button parts.");
        }
        return iconIdOrFirstSelectedLine;
    }

    public int getIconId() {
        if (partType == PartType.FIELD) {
            throw new IllegalStateException("This value does not apply to field parts.");
        }
        return iconIdOrFirstSelectedLine;
    }

    public TextAlignment getTextAlign() {
        return textAlign;
    }

    public short getSize() {
        return size;
    }

    public short getPartId() {
        return partId;
    }

    public PartType getPartType() {
        return partType;
    }

    public PartFlag[] getFlags() {
        return flags;
    }

    public short getTop() {
        return top;
    }

    public short getLeft() {
        return left;
    }

    public short getBottom() {
        return bottom;
    }

    public short getRight() {
        return right;
    }

    public ExtendedPartFlag[] getExtendedFlags() {
        return extendedFlags;
    }

    public int getFamily() {
        return family;
    }

    public PartStyle getStyle() {
        return style;
    }

    public short getTitleWidthOrLastSelectedLine() {
        return titleWidthOrLastSelectedLine;
    }

    public short getIconIdOrFirstSelectedLine() {
        return iconIdOrFirstSelectedLine;
    }

    public short getTextFontId() {
        return textFontId;
    }

    public short getTextSize() {
        return textSize;
    }

    public FontStyle[] getFontStyles() {
        return fontStyles;
    }

    public short getTextHeight() {
        return textHeight;
    }

    public String getName() {
        return name;
    }

    public String getScript() {
        return script;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
