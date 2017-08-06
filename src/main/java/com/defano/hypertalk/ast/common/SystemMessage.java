package com.defano.hypertalk.ast.common;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

public enum SystemMessage {
    MOUSE_UP("mouseUp", "Sent when the user presses and releases the mouse over this part."),
    MOUSE_DOWN("mouseDown", "Sent when the user presses the mouse over this part."),
    MOUSE_ENTER("mouseEnter", "Sent when the mouse enters the rectangle of this part."),
    MOUSE_LEAVE("mouseLeave", "Sent when the mouse leaves the rectangle of this part."),
    MOUSE_DOUBLE_CLICK("mouseDoubleClick", "Send when the user double-clicks this part."),
    KEY_DOWN("keyDown", "Sent when the user types a key into this part.", "theKey"),
    ARROW_KEY("arrowKey", "Sent when the user types an arrow key into this part."),
    COMMAND_KEY("commandKeyDown", "Sent when the user presses the command key."),
    CONTROL_KEY("controlKey", "Sent when the user presses the control key."),
    ENTER_KEY("enterKey", "Sent when the user presses the enter key."),
    ENTER_IN_FIELD("enterInField", "Sent to fields when the user types enter in the field.", PartType.FIELD),
    RETURN_IN_FIELD("returnInField", "Sent to fields when the user types return in the field.", PartType.FIELD),
    FUNCTION_KEY("functionKey", "Sent when the user presses a function key."),
    RETURN_KEY("returnKey", "Sent when the user presses the return key."),
    TAB_KEY("tabKey", "Sent when the user presses the tab key."),
    DO_MENU("doMenu", "Sent when the user chooses a menu item from the menu bar.", "theMenu", "theMenuItem");

    public final String messageName;
    public final String description;
    private final PartType sentOnlyTo;
    public final String[] arguments;

    SystemMessage(String  messageName, String description) {
        this.messageName = messageName;
        this.description = description;
        this.arguments = null;
        this.sentOnlyTo = null;
    }

    SystemMessage(String  messageName, String description, String... arguments) {
        this.messageName = messageName;
        this.description = description;
        this.arguments = arguments;
        this.sentOnlyTo = null;
    }

    SystemMessage(String messageName, String description, PartType sentOnlyTo) {
        this.messageName = messageName;
        this.description = description;
        this.arguments = null;
        this.sentOnlyTo = sentOnlyTo;
    }

    public static Collection<SystemMessage> messagesSentTo(PartType partType) {
        ArrayList<SystemMessage> messages = new ArrayList<>();
        for (SystemMessage thisMessage : SystemMessage.values()) {
            if (thisMessage.sentOnlyTo == null || thisMessage.sentOnlyTo == partType) {
                messages.add(thisMessage);
            }
        }

        return messages;
    }

    public static SystemMessage fromHandlerName(String handlerName) {
        for (SystemMessage thisMessage : values()) {
            if (thisMessage.messageName.equalsIgnoreCase(handlerName)) {
                return thisMessage;
            }
        }

        return null;
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
