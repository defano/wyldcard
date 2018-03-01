package com.defano.hypercard.border;

import com.defano.hypercard.awt.KeyboardManager;

import java.awt.*;

public interface ColorStateBorder {

    default Color getBorderColor(Component c) {
        if (KeyboardManager.getInstance().isCommandOptionDown()) {
            return SystemColor.textHighlight;
        } else {
            return c.isEnabled() ? Color.BLACK : Color.GRAY;
        }
    }

}
