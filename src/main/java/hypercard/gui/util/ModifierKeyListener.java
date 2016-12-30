package hypercard.gui.util;

import java.awt.*;
import java.awt.event.KeyEvent;

public class ModifierKeyListener {

    public static boolean isShiftDown;
    public static boolean isBreakSequence;

    public static void start() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            isShiftDown = e.isShiftDown();
            isBreakSequence = e.getKeyCode() == KeyEvent.VK_PERIOD && (e.isMetaDown() || e.isControlDown());

            return false;
        });
    }
}
