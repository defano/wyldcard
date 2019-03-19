package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.enums.PrintMeasurementUnit;
import com.defano.wyldcard.stackreader.enums.ReportFlag;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.ReportRecord;

import java.io.IOException;

@SuppressWarnings("unused")
public class PrintReportBlock extends Block {

    private PrintMeasurementUnit units;
    private short marginTop;
    private short marginLeft;
    private short marginRight;
    private short marginBottom;
    private short spacingHeight;
    private short spacingWidth;
    private short cellHeight;
    private short cellWidth;
    private ReportFlag[] flags = new ReportFlag[0];
    private byte headerLength;
    private String header;
    private int reportRecordCount;
    private ReportRecord[] reportRecords = new ReportRecord[0];

    public PrintReportBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public PrintMeasurementUnit getUnits() {
        return units;
    }

    public short getMarginTop() {
        return marginTop;
    }

    public short getMarginLeft() {
        return marginLeft;
    }

    public short getMarginRight() {
        return marginRight;
    }

    public short getMarginBottom() {
        return marginBottom;
    }

    public short getSpacingHeight() {
        return spacingHeight;
    }

    public short getSpacingWidth() {
        return spacingWidth;
    }

    public short getCellHeight() {
        return cellHeight;
    }

    public short getCellWidth() {
        return cellWidth;
    }

    public ReportFlag[] getFlags() {
        return flags;
    }

    public byte getHeaderLength() {
        return headerLength;
    }

    public String getHeader() {
        return header;
    }

    public int getReportRecordCount() {
        return reportRecordCount;
    }

    public ReportRecord[] getReportRecords() {
        return reportRecords;
    }

    @Override
    public void unpack(ImportResult report) throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            units = PrintMeasurementUnit.fromByte(sis.readByte());
            sis.skipBytes(1);
            marginTop = sis.readShort();
            marginLeft = sis.readShort();
            marginBottom = sis.readShort();
            marginRight = sis.readShort();
            spacingHeight = sis.readShort();
            spacingWidth = sis.readShort();
            cellHeight = sis.readShort();
            cellWidth = sis.readShort();
            flags = ReportFlag.fromBitmask(sis.readShort());
            headerLength = sis.readByte();
            header = sis.readString(headerLength);

            sis.skipToOffset(0x124 - 16);
            reportRecordCount = sis.readShort();

            reportRecords = new ReportRecord[reportRecordCount];
            for (int idx = 0; idx < reportRecordCount; idx++) {
                int recordSize = sis.readShort();
                byte[] recordData = sis.readBytes(recordSize - 2);

                reportRecords[idx] = ReportRecord.deserialize(this, recordSize, recordData, report);
            }

        } catch (IOException e) {
            report.throwError(this, "Malformed PRFT block.");
        }
    }
}
