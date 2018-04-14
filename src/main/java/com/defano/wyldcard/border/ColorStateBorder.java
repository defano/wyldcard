package com.defano.wyldcard.border;

import com.defano.wyldcard.awt.KeyboardManager;

import java.awt.*;

public interface ColorStateBorder {

    default Color getBorderColor(Component c) {
        if (KeyboardManager.getInstance().isPeeking()) {
            return SystemColor.textHighlight;
        } else {
            return c.isEnabled() ? Color.BLACK : Color.GRAY;
        }
    }

}
