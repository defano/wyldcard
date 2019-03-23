package com.defano.wyldcard.stackreader.record;

import com.defano.wyldcard.stackreader.misc.ImportException;
import com.defano.wyldcard.stackreader.misc.StackInputStream;
import com.defano.wyldcard.stackreader.block.Block;
import com.defano.wyldcard.stackreader.misc.ImportResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.IOException;

@SuppressWarnings("unused")
public class PartContentRecord {

    private short partId;
    private boolean isPlaintext;
    private StyleSpanRecord[] styleSpans = new StyleSpanRecord[0];
    private String text;

    public static PartContentRecord deserialize(Block parent, short partId, byte[] data, ImportResult report) throws ImportException {
        PartContentRecord partContent = new PartContentRecord();
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

                partContent.styleSpans = new StyleSpanRecord[styleLength / 4];
                for (int styleIdx = 0; styleIdx < styleLength / 4; styleIdx++) {
                    short textPosition = sis.readShort();
                    short styleId = sis.readShort();
                    partContent.styleSpans[styleIdx] = new StyleSpanRecord(textPosition, styleId);
                }
            }

            // When bit is clear, contents are plaintext
            else {
                partContent.isPlaintext = true;
            }

            partContent.text = sis.readString(length - styleLength);

            System.err.println(partContent);

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

    public StyleSpanRecord[] getStyleSpans() {
        return styleSpans;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
