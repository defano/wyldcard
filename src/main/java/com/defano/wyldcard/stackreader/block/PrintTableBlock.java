package com.defano.wyldcard.stackreader.block;

import com.defano.wyldcard.stackreader.HyperCardStack;
import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.record.ReportTemplateRecord;

import java.io.IOException;

@SuppressWarnings("unused")
public class PrintTableBlock extends Block {

    private short prstBlockId;
    private short reportTemplateCount;
    private ReportTemplateRecord[] templateRecords = new ReportTemplateRecord[0];

    public PrintTableBlock(HyperCardStack stack, BlockType blockType, int blockSize, int blockId, byte[] blockData) {
        super(stack, blockType, blockSize, blockId, blockData);
    }

    public short getPrstBlockId() {
        return prstBlockId;
    }

    public short getReportTemplateCount() {
        return reportTemplateCount;
    }

    public ReportTemplateRecord[] getTemplateRecords() {
        return templateRecords;
    }

    @Override
    public void unpack() throws ImportException {
        StackInputStream sis = new StackInputStream(getBlockData());

        try {
            sis.skipBytes(32);
            prstBlockId = sis.readShort();
            sis.skipBytes(258);
            reportTemplateCount = sis.readShort();

            templateRecords = new ReportTemplateRecord[reportTemplateCount];
            for (int idx = 0; idx < reportTemplateCount; idx++) {
                int templateId = sis.readInt();
                byte nameLength = sis.readByte();
                String templateName = sis.readString(nameLength);

                templateRecords[idx] = new ReportTemplateRecord(templateId, templateName);
                sis.skipBytes(36 - nameLength - 5);
            }

        } catch (IOException e) {
            throw new ImportException(this, "Malformed PRNT block.", e);
        }
    }
}
