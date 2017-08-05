package com.defano.hypertalk.ast.common;

import java.awt.event.KeyEvent;

public enum SystemMessage {
    MOUSE_UP("mouseUp"),
    MOUSE_DOWN("mouseDown"),
    MOUSE_ENTER("mouseEnter"),
    MOUSE_LEAVE("mouseLeave"),
    MOUSE_DOUBLE_CLICK("mouseDoubleClick"),
    KEY_DOWN("keyDown"),
    ARROW_KEY("arrowKey"),
    COMMAND_KEY("commandKeyDown"),
    CONTROL_KEY("controlKey"),
    ENTER_KEY("enterKey"),
    ENTER_IN_FIELD("enterInField"),
    RETURN_IN_FIELD("returnInField"),
    FUNCTION_KEY("functionKey"),
    RETURN_KEY("returnKey"),
    TAB_KEY("tabKey"),
    DO_MENU("doMenu");

    public final String messageName;

    SystemMessage(String messageName) {
        this.messageName = messageName;
    }

    public static SystemMessage fromKeyEvent(KeyEvent e, boolean inField) {
        if (e.isControlDown()) {
            return CONTROL_KEY;
        }

        if (e.isMetaDown()) {
            return COMMAND_KEY;
        }

        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            return TAB_KEY;
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            return inField ? ENTER_IN_FIELD : ENTER_KEY;
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD) {
            return inField ? RETURN_IN_FIELD : RETURN_KEY;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT ||
            e.getKeyCode() == KeyEvent.VK_RIGHT ||
            e.getKeyCode() == KeyEvent.VK_UP ||
            e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            return ARROW_KEY;
        }

        if (e.getKeyCode() == KeyEvent.VK_F1 ||
            e.getKeyCode() == KeyEvent.VK_F2 ||
            e.getKeyCode() == KeyEvent.VK_F3 ||
            e.getKeyCode() == KeyEvent.VK_F4 ||
            e.getKeyCode() == KeyEvent.VK_F5 ||
            e.getKeyCode() == KeyEvent.VK_F6 ||
            e.getKeyCode() == KeyEvent.VK_F7 ||
            e.getKeyCode() == KeyEvent.VK_F8 ||
            e.getKeyCode() == KeyEvent.VK_F9 ||
            e.getKeyCode() == KeyEvent.VK_F10 ||
            e.getKeyCode() == KeyEvent.VK_F11 ||
            e.getKeyCode() == KeyEvent.VK_F12)
        {
            return FUNCTION_KEY;
        }

        return null;
    }
}
