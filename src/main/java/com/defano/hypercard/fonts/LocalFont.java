/*
 * LocalFont
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.fonts;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public enum LocalFont {
    CHICAGO("/fonts/pixChicago.ttf", "pix chicago", "chicago");

    public final String resource;
    public final List<String> familyNames;

    LocalFont(String resource, String... familyNames) {
        this.resource = resource;
        this.familyNames = Arrays.asList(familyNames);
    }

    public static LocalFont forName(String named) {
        for (LocalFont thisFont : LocalFont.values()) {
            if (thisFont.familyNames.contains(named.toLowerCase())) {
                return thisFont;
            }
        }

        return null;
    }

    public static boolean isLocalFont(String name) {
        return forName(name) != null;
    }

    public Font load(int style, float size) {
        try {
            InputStream is = FontUtils.class.getResourceAsStream(resource);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size).deriveFont(style);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
