package com.defano.wyldcard.importer.type;

import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.block.Block;
import com.defano.wyldcard.importer.result.ImportResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.awt.*;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class Part {

    private short size; // size of the part, including this header
    private short partId; // ID number of the part
    private PartType partType;
    private List<PartFlag> flags;
    private short top; // top of the part's rectangle
    private short left; // left of the part's rectangle
    private short bottom; // bottom of the part's rectangle
    private short right; // right of the part's rectangle
    private List<ExtendedPartFlag> extendedFlags;
    private int family;
    private PartStyle style; // HyperCard only looks at the least significant 4 bits of this field
    private short titleWidthOrLastSelectedLine;
    private short iconIdOrFirstSelectedLine;
    private TextAlignment textAlign;
    private short textFontId; // use the FTBL block to translate this to a font name
    private short textSize;
    private List<TextStyle> textStyles;
    private short textHeight;
    private String name;
    private String script; // the button or field script

    public static Part deserialize(Block parent, short entrySize, byte[] data, ImportResult report) throws ImportException {
        Part part = new Part();
        StackInputStream sis = new StackInputStream(data);

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
            part.textStyles = TextStyle.fromBitmask(sis.readByte());
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
