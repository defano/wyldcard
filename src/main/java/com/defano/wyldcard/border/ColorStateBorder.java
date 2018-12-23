package com.defano.wyldcard.border;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.*;

public interface ColorStateBorder {

    default Color getBorderColor(Component c) {
        if (WyldCard.getInstance().getKeyboardManager().isPeeking(new ExecutionContext())) {
            return SystemColor.textHighlight;
        } else {
            return c.isEnabled() ? Color.BLACK : Color.GRAY;
        }
    }

}
