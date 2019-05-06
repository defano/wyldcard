package com.defano.wyldcard.fonts;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * A utility for resolving fonts from HyperTalk identifiers.
 */
public class FontUtils {

    public static int getAlignmentStyleForValue(Value v) {
        switch (v.toString().trim().toLowerCase()) {
            case "left":
                return StyleConstants.ALIGN_LEFT;
            case "right":
                return StyleConstants.ALIGN_RIGHT;

            default:
            case "center":
                return StyleConstants.ALIGN_CENTER;
        }
    }

    public static int getAlignmentForValue(Value v) {
        switch (v.toString().trim().toLowerCase()) {
            case "left":
                return SwingConstants.LEFT;
            case "right":
                return SwingConstants.RIGHT;

            default:
            case "center":
                return SwingConstants.CENTER;
        }
    }

    public static int getFontStyleForValue(ExecutionContext context, Value v) {
        int style = Font.PLAIN;

        for (Value thisValue : v.getItems(context)) {
            switch (thisValue.toString().trim().toLowerCase()) {
                case "plain":
                    style = Font.PLAIN;
                    break;
                case "italic":
                    style |= Font.ITALIC;
                    break;
                case "bold":
                    style |= Font.BOLD;
                    break;
            }
        }

        return style;
    }

    public static Font getFontByNameStyleSize(String name, int style, int size) {
        if (LocalFont.isLocalFont(name)) {
            return LocalFont.forName(name).load(style, size);
        } else {
            return new Font(name, style, size);
        }
    }
}
