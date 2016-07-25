package hypercard.gui.util;

import java.awt.*;

public class ModifierKeyListener {

    public static boolean isShiftDown;

    static {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            isShiftDown = e.isShiftDown();
            return false;
        });
    }
}
