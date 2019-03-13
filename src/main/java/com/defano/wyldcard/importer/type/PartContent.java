package com.defano.wyldcard.importer.type;

import com.defano.wyldcard.importer.StackInputStream;

import java.io.IOException;

public class PartContent {

    private short partId;
    private boolean isPlaintext;
    private StyleSpan[] styleSpans;

    public PartContent(short partId, byte[] data) {
        StackInputStream sis = new StackInputStream(data);

        this.partId = partId;

        try {
            byte plaintextMarker = sis.readByte();

            if (plaintextMarker == 0) {
                isPlaintext = true;
            } else {
                byte low = sis.readByte();
                short styleLength = (short) (((plaintextMarker & 0xe0) << 8) | (low & 0xff));

                styleSpans = new StyleSpan[styleLength / 4];
                for (int styleIdx = 0; styleIdx < styleLength / 4; styleIdx++) {
                    short textPosition = sis.readShort();
                    short styleId = sis.readShort();

                    styleSpans[styleIdx] = new StyleSpan(textPosition, styleId);
                }
            }

            System.err.println(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
