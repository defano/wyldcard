package com.defano.wyldcard.message;

import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An enumeration of messages implicitly sent by HyperCard as a user interacts with a stack.
 */
public enum SystemMessage implements Message {

    MOUSE_UP("mouseUp", "Sent when the user presses and releases the mouse over this part."),
    MOUSE_DOWN("mouseDown", "Sent when the user presses the mouse over this part."),
    MOUSE_STILL_DOWN("mouseStillDown", "Sent when the user presses and holds the mouse over this part."),
    MOUSE_ENTER("mouseEnter", "Sent when the mouse enters the rectangle of this part."),
    MOUSE_LEAVE("mouseLeave", "Sent when the mouse leaves the rectangle of this part."),
    MOUSE_DOUBLE_CLICK("mouseDoubleClick", "Sent when the user double-clicks this part."),
    MOUSE_WITHIN("mouseWithin", "Sent periodically while the mouse is within this part.",
            new PartType[]{PartType.FIELD, PartType.BUTTON}),
    RESUME_STACK("resumeStack", "Sent to the card when this stack's window gains focus.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    SUSPEND_STACK("suspendStack", "Sent to the card when this stack's window loses focus.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    OPEN_STACK("openStack", "Sent when the stack is opened.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    OPEN_CARD("openCard", "Sent just after the user navigates to this card.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    CLOSE_CARD("closeCard", "Sent just before the user navigates away from this card.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    NEW_CARD("newCard", "Sent when a new card is added to the stack.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    NEW_FIELD("newField", "Sent when a new field is added to the card or background.",
            new PartType[]{PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    NEW_BUTTON("newButton", "Sent when a new button is added to the card or background.",
            new PartType[]{PartType.BUTTON, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    DELETE_CARD("deleteCard", "Sent when a card is removed from the stack.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    KEY_DOWN("keyDown", "Sent when the user types a key into this part.",
            new String[]{"theKey"},
            new PartType[]{PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    ARROW_KEY("arrowKey", "Sent when the user types an arrow key into this part.",
            new String[]{"direction"},
            new PartType[]{PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    COMMAND_KEY("commandKeyDown", "Sent when the user presses the command key.",
            new String[]{"whichKey"},
            new PartType[]{PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    CONTROL_KEY("controlKey", "Sent when the user presses the control key.",
            new String[]{"whichKey"},
            new PartType[]{PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    ENTER_KEY("enterKey", "Sent when the user presses the enter key.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    ENTER_IN_FIELD("enterInField", "Sent to fields when the user types enter in the field.",
            new PartType[]{PartType.FIELD}),
    EXIT_FIELD("exitField", "Sent to a field when it loses focus.",
            new PartType[]{PartType.FIELD}),
    OPEN_FIELD("openField", "Sent to a field when it gains focus.",
            new PartType[]{PartType.FIELD}),
    RETURN_IN_FIELD("returnInField", "Sent to fields when the user types return in the field.",
            new PartType[]{PartType.FIELD}),
    FUNCTION_KEY("functionKey", "Sent when the user presses a function key.",
            new String[]{"whichKey"},
            new PartType[]{PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    RETURN_KEY("returnKey", "Sent when the user presses the return key.",
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    TAB_KEY("tabKey", "Sent when the user presses the tab key.",
            new PartType[]{PartType.FIELD, PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    DO_MENU("doMenu", "Sent when the user chooses a menu item from the menu bar.",
            new String[]{"theMenu", "theMenuItem"},
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    CHOOSE("choose", "Sent when the tool selection changes.",
            new String[]{"what", "toolNumber"},
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK}),
    IDLE("idle", "Sent repeatedly whenever no scripts are executing.",
            new String[]{},
            new PartType[]{PartType.CARD, PartType.BACKGROUND, PartType.STACK});

    public final String messageName;
    public final String description;
    public final String[] arguments;
    private final List<PartType> sentOnlyTo = new ArrayList<>();

    SystemMessage(String messageName, String description) {
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

    /**
     * Determines if the message is impacted by the 'lockMessages' system property.
     *
     * @param message The message name
     * @return True if the lockMessages property prevents the message from being sent.
     */
    public static boolean isLockable(String message) {
        SystemMessage systemMessage = SystemMessage.fromHandlerName(message);
        return systemMessage == OPEN_CARD || systemMessage == OPEN_STACK || systemMessage == CLOSE_CARD;
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

    public static Message fromKeyEvent(KeyEvent e, boolean inField) {
        ExecutionContext context = new ExecutionContext();

        if (e.isControlDown() && e.getKeyCode() != KeyEvent.VK_CONTROL) {
            return MessageBuilder.named(CONTROL_KEY.getMessageName(context)).withArgument(e.getKeyCode()).build();
        }

        if (e.isMetaDown() && e.getKeyCode() != KeyEvent.VK_META) {
            return MessageBuilder.named(COMMAND_KEY.getMessageName(context)).withArgument(e.getKeyCode()).build();
        }

        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            return MessageBuilder.named(TAB_KEY.getMessageName(context)).build();
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
            return inField ?
                    MessageBuilder.named(ENTER_IN_FIELD.getMessageName(context)).build() :
                    MessageBuilder.named(ENTER_KEY.getMessageName(context)).build();
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD) {
            return inField ?
                    MessageBuilder.named(RETURN_IN_FIELD.getMessageName(context)).build() :
                    MessageBuilder.named(RETURN_KEY.getMessageName(context)).build();
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            return MessageBuilder.named(ARROW_KEY.getMessageName(context)).withArgument("left").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            return MessageBuilder.named(ARROW_KEY.getMessageName(context)).withArgument("right").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            return MessageBuilder.named(ARROW_KEY.getMessageName(context)).withArgument("up").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            return MessageBuilder.named(ARROW_KEY.getMessageName(context)).withArgument("down").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F1) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("1").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F2) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("2").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F3) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("3").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F4) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("4").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F5) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("5").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F6) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("6").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F7) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("7").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F8) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("8").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F9) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("9").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F10) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("10").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F11) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("11").build();
        }

        if (e.getKeyCode() == KeyEvent.VK_F12) {
            return MessageBuilder.named(FUNCTION_KEY.getMessageName(context)).withArgument("12").build();
        }

        return null;
    }

    @Override
    public String getMessageName(ExecutionContext context) {
        return messageName;
    }

    @Override
    public List<Value> getArguments(ExecutionContext context) {
        if (arguments == null) {
            return new ArrayList<>();
        } else {
            return Arrays.stream(arguments).map(Value::new).collect(Collectors.toList());
        }
    }
}
