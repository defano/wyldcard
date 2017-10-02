package com.defano.hypertalk.utils;

import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * A helper to get and set the properties of menu items.
 *
 * Why this special contraption for menus?
 *
 * Menu items are not "first class" parts in HyperCard and so they are not modeled like other parts are. Note that menu
 * properties are ephemeral and are not saved as part of the stack, and are therefore not automatically restored when
 * opening the stack.
 */
public class MenuPropertiesDelegate {

    private static final String PROP_ENABLED = "enabled";
    private static final String PROP_CHECKMARK = "checkmark";
    private static final String PROP_COMMANDCHAR = "commandchar";
    private static final String PROP_NAME = "name";

    public static Value getProperty(String name, MenuItemSpecifier menuItem) throws HtSemanticException {
        switch (name.toLowerCase()) {
            case PROP_ENABLED:
                return new Value(menuItem.getSpecifiedMenuItem().isEnabled());
            case PROP_CHECKMARK:
                return new Value(menuItem.getSpecifiedMenuItem().isSelected());
            case PROP_COMMANDCHAR:
                return new Value(menuItem.getSpecifiedMenuItem().getAccelerator().getKeyChar());
            case PROP_NAME:
                return new Value(menuItem.getSpecifiedMenuItem().getText());
            default:
                throw new HtSemanticException(name + " is not a menu item property.");
        }
    }

    public static void setProperty(String name, Value value, MenuItemSpecifier menuItem) throws HtSemanticException {
        switch (name.toLowerCase()) {
            case PROP_ENABLED:
                menuItem.getSpecifiedMenuItem().setEnabled(value.booleanValue());
                break;
            case PROP_CHECKMARK:
                makeCheckable(value.booleanValue(), menuItem.getSpecifiedItemIndex(), menuItem.getSpecifiedMenu());
                menuItem.getSpecifiedMenuItem().setSelected(value.booleanValue());
                break;
            case PROP_COMMANDCHAR:
                if (value.stringValue().length() == 0) {
                    menuItem.getSpecifiedMenuItem().setAccelerator(null);
                } else {
                    char accelerator = value.stringValue().toUpperCase().charAt(0);
                    menuItem.getSpecifiedMenuItem().setAccelerator(KeyStroke.getKeyStroke(accelerator, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                }
                break;
            case PROP_NAME:
                menuItem.getSpecifiedMenuItem().setText(value.stringValue());
            default:
                throw new HtSemanticException(name + " is not a menu item property.");
        }
    }

    private static void makeCheckable(boolean checkable, int index, JMenu menu) {
        JMenuItem oldItem = menu.getItem(index);
        JMenuItem newItem = checkable ? new JCheckBoxMenuItem() : new JMenuItem();

        newItem.setText(oldItem.getText());
        newItem.setAccelerator(oldItem.getAccelerator());
        newItem.setIcon(oldItem.getIcon());

        for (ActionListener thisActionListener : oldItem.getActionListeners()) {
            newItem.addActionListener(thisActionListener);
        }

        menu.remove(index);
        menu.add(newItem, index);
    }
}
