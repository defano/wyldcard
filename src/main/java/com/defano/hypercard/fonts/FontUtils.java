package com.defano.hypercard.fonts;

import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class FontUtils {

    public static int getAlignmentStyleForValue(Value v) {
        switch (v.stringValue().trim().toLowerCase()) {
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
        switch (v.stringValue().trim().toLowerCase()) {
            case "left":
                return SwingUtilities.LEFT;
            case "right":
                return SwingUtilities.RIGHT;

            default:
            case "center":
                return SwingUtilities.CENTER;
        }
    }

    public static int getStyleForValue(Value v) {
        int style = Font.PLAIN;

        for (Value thisValue : v.getItems()) {
            switch (thisValue.stringValue().trim().toLowerCase()) {
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

    public static Value getValueForStyle(int fontStyle) {
        if (fontStyle == Font.ITALIC) {
            return new Value("italic");
        } else if (fontStyle == Font.BOLD) {
            return new Value("bold");
        } else if (fontStyle == (Font.BOLD | Font.ITALIC)) {
            return new Value("bold, italic");
        }

        return new Value("plain");
    }

}
