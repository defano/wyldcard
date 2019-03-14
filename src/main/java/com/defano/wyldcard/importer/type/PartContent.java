package com.defano.wyldcard.importer.type;

import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.StackInputStream;
import com.defano.wyldcard.importer.block.Block;
import com.defano.wyldcard.importer.result.ImportResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.IOException;

@SuppressWarnings("unused")
public class PartContent {

    private short partId;
    private boolean isPlaintext;
    private StyleSpan[] styleSpans = new StyleSpan[0];
    private String contents;

    public static PartContent deserialize(Block parent, short partId, byte[] data, ImportResult report) throws ImportException {
        PartContent partContent = new PartContent();
        StackInputStream sis = new StackInputStream(data);

        partContent.partId = partId;

        int length = data.length;
        int styleLength = 1;

        try {
            byte highByte = sis.readByte();

            // When high bit is set, data provides a table of style spans
            if ((highByte & 0x80) > 0) {
                byte lowByte = sis.readByte();

                styleLength = ((highByte & 0x7f) << 8) | (lowByte & 0xff);

                partContent.styleSpans = new StyleSpan[styleLength / 4];
                for (int styleIdx = 0; styleIdx < styleLength / 4; styleIdx++) {
                    short textPosition = sis.readShort();
                    short styleId = sis.readShort();
                    partContent.styleSpans[styleIdx] = new StyleSpan(textPosition, styleId);
                }
            }

            // When bit is clear, contents are plaintext
            else {
                partContent.isPlaintext = true;
            }

            partContent.contents = sis.readString(length - styleLength);

        } catch (IOException e) {
            report.throwError(parent, "Malformed part content record; stack is corrupt.");
        }

        return partContent;
    }

    public short getPartId() {
        return partId;
    }

    public boolean isPlaintext() {
        return isPlaintext;
    }

    public StyleSpan[] getStyleSpans() {
        return styleSpans;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
