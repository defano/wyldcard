package com.defano.wyldcard.importer.record;

import com.defano.wyldcard.importer.block.Block;
import com.defano.wyldcard.importer.enums.FontStyle;
import com.defano.wyldcard.importer.enums.ReportRecordFlag;
import com.defano.wyldcard.importer.enums.TextAlignment;
import com.defano.wyldcard.importer.misc.ImportException;
import com.defano.wyldcard.importer.misc.ImportResult;
import com.defano.wyldcard.importer.misc.StackInputStream;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.IOException;

@SuppressWarnings("unused")
public class ReportRecord {

    private int recordSize;
    private int top;
    private int left;
    private int bottom;
    private int right;
    private int columnCount;
    private ReportRecordFlag[] flags = new ReportRecordFlag[0];
    private short textSize;
    private short textHeight;
    private FontStyle[] textStyles = new FontStyle[0];
    private TextAlignment textAlignment;
    private String contents;
    private String textFont;

    public int getRecordSize() {
        return recordSize;
    }

    public int getTop() {
        return top;
    }

    public int getLeft() {
        return left;
    }

    public int getBottom() {
        return bottom;
    }

    public int getRight() {
        return right;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public ReportRecordFlag[] getFlags() {
        return flags;
    }

    public short getTextSize() {
        return textSize;
    }

    public short getTextHeight() {
        return textHeight;
    }

    public FontStyle[] getTextStyles() {
        return textStyles;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public String getContents() {
        return contents;
    }

    public String getTextFont() {
        return textFont;
    }

    public static ReportRecord deserialize(Block block, int recordSize, byte[] recordData, ImportResult result) throws ImportException {
        StackInputStream sis = new StackInputStream(recordData);
        ReportRecord reportRecord = new ReportRecord();

        reportRecord.recordSize = recordSize;

        try {
            reportRecord.top = sis.readShort();
            reportRecord.left = sis.readShort();
            reportRecord.bottom = sis.readShort();
            reportRecord.right = sis.readShort();
            reportRecord.columnCount = sis.readShort();
            reportRecord.flags = ReportRecordFlag.fromBitmask(sis.readShort());
            reportRecord.textSize = sis.readShort();
            reportRecord.textHeight = sis.readShort();
            reportRecord.textStyles = FontStyle.fromBitmask((byte) sis.readShort());
            reportRecord.textAlignment = TextAlignment.fromAlignmentId(sis.readShort());
            reportRecord.contents = sis.readString();
            reportRecord.textFont = sis.readString();

        } catch (IOException e) {
            result.throwError(block, "Malformed report record.");
        }

        return reportRecord;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
