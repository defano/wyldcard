package com.defano.hypercard.cursor;

import com.defano.hypertalk.ast.common.Value;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An enumeration of HyperCard mouse cursors.
 */
public enum HyperCardCursor {
    I_BEAM("ibeam", Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)),
    CROSS("cross", Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)),
    PLUS("plus", Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)),
    WATCH("watch", Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)),
    HAND("hand", Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)),
    ARROW("arrow", Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)),
    BUSY("busy", Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)),
    NONE("none", getBlankCursor());

    public final String hyperTalkName;
    public final Cursor cursor;

    HyperCardCursor(String hyperTalkName, Cursor cursor) {
        this.hyperTalkName = hyperTalkName;
        this.cursor = cursor;
    }

    public static HyperCardCursor fromHyperTalkName(Value identifier) {
        for (HyperCardCursor thisCursor : values()) {
            if (thisCursor.hyperTalkName.equalsIgnoreCase(identifier.stringValue())) {
                return thisCursor;
            }
        }

        return null;
    }

    private static Cursor getBlankCursor() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        return Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Blank");
    }
}
