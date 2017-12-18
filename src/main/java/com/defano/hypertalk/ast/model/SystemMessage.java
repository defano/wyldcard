package com.defano.hypertalk.ast.model;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum SystemMessage {
    MOUSE_UP("mouseUp", "Sent when the user presses and releases the mouse over this part."),
    MOUSE_DOWN("mouseDown", "Sent when the user presses the mouse over this part."),
    MOUSE_STILL_DOWN("mouseStillDown", "Sent when the user presses and holds the mouse over this part."),
    MOUSE_ENTER("mouseEnter", "Sent when the mouse enters the rectangle of this part."),
    MOUSE_LEAVE("mouseLeave", "Sent when the mouse leaves the rectangle of this part."),
    MOUSE_DOUBLE_CLICK("mouseDoubleClick", "Sent when the user double-clicks this part."),
    MOUSE_WITHIN("mouseWithin", "Sent periodically while the mouse is within this part.",
            new PartType[] {PartType.FIELD, PartType.BUTTON}),
    OPEN_STACK("openStack", "Sent when the stack is opened.",
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    OPEN_CARD("openCard", "Sent just after the user navigates to this card.",
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    CLOSE_CARD("closeCard", "Sent just before the user navigates away from this card.",
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    NEW_CARD("newCard", "Sent when a new card is added to the stack.",
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    DELETE_CARD("deleteCard", "Sent when a card is removed from the stack.",
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    KEY_DOWN("keyDown", "Sent when the user types a key into this part.",
            new String[] {"theKey"},
            new PartType[] {PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    ARROW_KEY("arrowKey", "Sent when the user types an arrow key into this part.",
            new String[] {"direction"},
            new PartType[] {PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    COMMAND_KEY("commandKeyDown", "Sent when the user presses the command key.",
            new PartType[] {PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    CONTROL_KEY("controlKey", "Sent when the user presses the control key.",
            new PartType[] {PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    ENTER_KEY("enterKey", "Sent when the user presses the enter key.",
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    ENTER_IN_FIELD("enterInField", "Sent to fields when the user types enter in the field.",
            new PartType[] {PartType.FIELD}),
    RETURN_IN_FIELD("returnInField", "Sent to fields when the user types return in the field.",
            new PartType[] {PartType.FIELD}),
    FUNCTION_KEY("functionKey", "Sent when the user presses a function key.",
            new String[] {"whichKey"},
            new PartType[] {PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    RETURN_KEY("returnKey", "Sent when the user presses the return key.",
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    TAB_KEY("tabKey", "Sent when the user presses the tab key.",
            new PartType[] {PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    DO_MENU("doMenu", "Sent when the user chooses a menu item from the menu bar.",
            new String[] {"theMenu", "theMenuItem"},
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    CHOOSE("choose", "Sent when the tool selection changes.",
            new String[] {"what", "toolNumber"},
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    IDLE("idle", "Sent repeatedly whenever no scripts are executing.",
            new String[] {},
            new PartType[] {PartType.CARD, PartType.BACKGROUND, PartType.STACK});

    public final String messageName;
    public final String description;
    private final List<PartType> sentOnlyTo = new ArrayList<>();
    public final String[] arguments;

    SystemMessage(String  messageName, String description) {
        this.messageName = messageName;
        this.description = description;
        this.arguments = null;
    }

    SystemMessage(String messageName, String description, PartType[] sentOnlyTo) {
        this.messageName = messageName;
        this.description = description;
        this.arguments = null;
        this.sentOnlyTo.addAll(Arrays.asList(sentOnlyTo));
    }

    SystemMessage(String messageName, String description, String[] arguments, PartType[] sentOnlyTo) {
        this.messageName = messageName;
        this.description = description;
        this.arguments = arguments;
        this.sentOnlyTo.addAll(Arrays.asList(sentOnlyTo));
    }

    public static Collection<SystemMessage> messagesSentTo(PartType partType) {
        ArrayList<SystemMessage> messages = new ArrayList<>();
        for (SystemMessage thisMessage : SystemMessage.values()) {
            if (thisMessage.sentOnlyTo.isEmpty() || thisMessage.sentOnlyTo.contains(partType)) {
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

    public static BoundSystemMessage fromKeyEvent(KeyEvent e, boolean inField) {
        if (e.isControlDown()) {
            return new BoundSystemMessage(CONTROL_KEY);
        }

        if (e.isMetaDown()) {
            return new BoundSystemMessage(COMMAND_KEY);
        }

        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            return new BoundSystemMessage(TAB_KEY);
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            return inField ? new BoundSystemMessage(ENTER_IN_FIELD) : new BoundSystemMessage(ENTER_KEY);
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD) {
            return inField ? new BoundSystemMessage(RETURN_IN_FIELD) : new BoundSystemMessage(RETURN_KEY);
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            return new BoundSystemMessage(ARROW_KEY, new Value("left"));
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            return new BoundSystemMessage(ARROW_KEY, new Value("right"));
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            return new BoundSystemMessage(ARROW_KEY, new Value("up"));
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            return new BoundSystemMessage(ARROW_KEY, new Value("down"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F1) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("1"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F2) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("2"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F3) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("3"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F4) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("4"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F5) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("5"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F6) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("6"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F7) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("7"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F8) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("8"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F9) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("9"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F10) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("10"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F11) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("11"));
        }

        if (e.getKeyCode() == KeyEvent.VK_F12) {
            return new BoundSystemMessage(FUNCTION_KEY, new Value("12"));
        }

        return null;
    }
}
