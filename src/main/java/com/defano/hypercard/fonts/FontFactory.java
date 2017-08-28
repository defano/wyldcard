/*
 * HyperCardFont
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

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
