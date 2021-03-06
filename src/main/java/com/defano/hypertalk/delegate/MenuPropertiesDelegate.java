package com.defano.hypertalk.delegate;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.MenuItemSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.menu.WyldCardMenuItem;
import com.defano.wyldcard.message.Message;
import com.defano.wyldcard.message.MessageBuilder;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A helper to get and set the properties of menu items.
 * <p>
 * Why this special contraption for menus?
 * <p>
 * Menu items are not "first class" parts in HyperCard and so they are not modeled like other parts are. Note that menu
 * properties are ephemeral and are not saved as part of the stack, and are therefore not automatically restored when
 * opening the stack.
 */
public class MenuPropertiesDelegate {

    private static final String PROP_ENABLED = "enabled";
    private static final String PROP_CHECKMARK = "checkmark";
    private static final String PROP_COMMANDCHAR = "commandchar";
    private static final String PROP_NAME = "name";
    private static final String PROP_MENUMESSAGE = "menumessage";
    private static final String ALIAS_MENUMESSAGE = "menumsg";

    public static Value getProperty(ExecutionContext context, String name, MenuItemSpecifier menuItem) throws HtException {
        return Invoke.onDispatch(() -> {
            switch (name.toLowerCase()) {
                case PROP_ENABLED:
                    return new Value(menuItem.getSpecifiedMenuItem(context).isEnabled());
                case PROP_CHECKMARK:
                    return new Value(menuItem.getSpecifiedMenuItem(context).isSelected());
                case PROP_COMMANDCHAR:
                    return new Value(menuItem.getSpecifiedMenuItem(context).getAccelerator().getKeyChar());
                case PROP_NAME:
                    return new Value(menuItem.getSpecifiedMenuItem(context).getText());
                case PROP_MENUMESSAGE:
                case ALIAS_MENUMESSAGE:
                    JMenuItem item = menuItem.getSpecifiedMenuItem(context);
                    if (item instanceof WyldCardMenuItem) {
                        Message menuMessage = ((WyldCardMenuItem) item).getMenuMessage();
                        if (menuMessage != null) {
                            return new Value(menuMessage.toEvaluatedMessage(context));
                        }
                    }
                    return new Value();
                default:
                    throw new HtSemanticException(name + " is not a menu item property.");
            }
        }, HtSemanticException.class);
    }

    public static void setProperty(ExecutionContext context, String name, Value value, MenuItemSpecifier menuItem) throws HtException {
        Invoke.onDispatch(() -> {
            switch (name.toLowerCase()) {
                case PROP_ENABLED:
                    menuItem.getSpecifiedMenuItem(context).setEnabled(value.booleanValue());
                    break;
                case PROP_CHECKMARK:
                    makeCheckable(value.booleanValue(), menuItem.getSpecifiedItemIndex(context), menuItem.getSpecifiedMenu(context));
                    menuItem.getSpecifiedMenuItem(context).setSelected(value.booleanValue());
                    break;
                case PROP_COMMANDCHAR:
                    if (value.toString().length() == 0) {
                        menuItem.getSpecifiedMenuItem(context).setAccelerator(null);
                    } else {
                        char accelerator = value.toString().toUpperCase().charAt(0);
                        menuItem.getSpecifiedMenuItem(context).setAccelerator(KeyStroke.getKeyStroke(accelerator, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                    }
                    break;
                case PROP_NAME:
                    menuItem.getSpecifiedMenuItem(context).setText(value.toString());
                case PROP_MENUMESSAGE:
                case ALIAS_MENUMESSAGE:
                    setMenuMessage(context, menuItem, value);
                    break;
                default:
                    throw new HtSemanticException(name + " is not a menu item property.");
            }
            return null;
        }, HtException.class);

    }

    private static void setMenuMessage(ExecutionContext context, MenuItemSpecifier menuItem, Value message) throws HtException {
        JMenuItem item = menuItem.getSpecifiedMenuItem(context);
        if (item instanceof WyldCardMenuItem) {
            WyldCardMenuItem wyldCardMenuItem = (WyldCardMenuItem) item;
            if (message.toString().trim().isEmpty()) {
                wyldCardMenuItem.setMenuMessage(null);
            } else {
                wyldCardMenuItem.setMenuMessage(MessageBuilder.fromString(message.toString()));
            }
        } else {
            throw new HtSemanticException("Can't set the message for that menu item.");
        }

    }

    @RunOnDispatch
    private static void makeCheckable(boolean checkable, int index, JMenu menu) {
        JMenuItem oldItem = menu.getItem(index);
        JMenuItem newItem = checkable ? new JCheckBoxMenuItem() : new JMenuItem();

        if (oldItem.getClass() == newItem.getClass()) {
            return;
        }

        newItem.setText(oldItem.getText());
        newItem.setAccelerator(oldItem.getAccelerator());
        newItem.setIcon(oldItem.getIcon());
        newItem.addActionListener(e -> Invoke.asynchronouslyOnDispatch(() -> newItem.setSelected(!newItem.isSelected())));

        for (ActionListener thisActionListener : oldItem.getActionListeners()) {
            newItem.addActionListener(thisActionListener);
        }

        menu.remove(index);
        menu.add(newItem, index);
    }
}
