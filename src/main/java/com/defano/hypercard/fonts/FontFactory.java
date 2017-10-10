package com.defano.hypercard.fonts;

import java.awt.*;

public class FontFactory {

    public static Font byNameStyleSize(String name, int style, int size) {
        if (LocalFont.isLocalFont(name)) {
            return LocalFont.forName(name).load(style, size);
        } else {
            return new Font(name, style, size);
        }
    }
}
