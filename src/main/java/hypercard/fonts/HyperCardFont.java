/*
 * HyperCardFont
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 10:07 AM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package hypercard.fonts;

import java.awt.*;

public class HyperCardFont {

    public static Font byNameStyleSize(String name, int style, int size) {
        if (LocalFont.isLocalFont(name)) {
            return LocalFont.forName(name).load(style, size);
        } else {
            return new Font(name, style, size);
        }
    }
}
