package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.menu.MenuItemBuilder;
import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.specifiers.MenuSpecifier;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuContainer extends Container {

    private final MenuSpecifier menu;
    private final MenuItemSpecifier item;

    public MenuContainer(MenuItemSpecifier item) {
        this.item = item;
        this.menu = null;
    }

    public MenuContainer(MenuSpecifier menu) {
        this.menu = menu;
        this.item = null;
    }

    @Override
    public Value getValue() throws HtSemanticException {
        if (item != null) {
            return getMenuItemValue(item.getSpecifiedMenu(), item.getSpecifiedItemIndex());
        } else if (menu != null) {
            return getMenuValue(menu.getSpecifiedMenu());
        }

        throw new IllegalStateException("Bug! Invalid container state.");
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        if (item != null) {
            putMenuItemValue(value, preposition);
        } else {
            putMenuValue(value, preposition);
        }
    }

    /**
     * Gets the value of a menu as a whole (a list of all menu items in the menu).
     *
     * @param menu The menu whose value should be returned.
     * @return The value of the menu
     */
    public static Value getMenuValue(JMenu menu)  {
        ArrayList<Value> menuItems = new ArrayList<>();
        for (int thisItemIndex = 0; thisItemIndex < menu.getItemCount(); thisItemIndex++) {
            JMenuItem thisItem = menu.getItem(thisItemIndex);
            if (thisItem == null || thisItem.getText() == null) {
                menuItems.add(new Value("-"));
            } else {
                menuItems.add(new Value(thisItem.getText()));
            }
        }

        return Value.ofLines(menuItems);
    }

    /**
     * Gets the value of a specific menu item.
     *
     * @param itemIndex The index of the menu item whose value should be returned.
     * @param menu The menu whose menu items should be returned.
     * @return The value of the specified menu item.
     */
    public static Value getMenuItemValue(JMenu menu, int itemIndex) throws HtSemanticException {
        if (itemIndex < 0 || itemIndex >= menu.getItemCount()) {
            throw new HtSemanticException("No such menu item " + (itemIndex + 1));
        }

        if (menu.getItem(itemIndex) == null) {
            return new Value("-");
        } else {
            return new Value(menu.getItem(itemIndex).getText());
        }
    }

    /**
     * Puts a Value into a menu relative to a given menu item. See {@link #addValueToMenu(Value, JMenu, int)}
     *
     * @param value the value representing new menu items.
     * @param preposition The preposition representing where items should be added relative to the given menu item.
     * @throws HtSemanticException Thrown if an error occurs adding items.
     */
    private void putMenuItemValue(Value value, Preposition preposition) throws HtSemanticException {
        JMenu menu = item.getSpecifiedMenu();
        int itemIndex = item.getSpecifiedItemIndex();       // Location of specified item

        if (preposition == Preposition.AFTER) {
            itemIndex++;
        }

        if (itemIndex < 0 || itemIndex > menu.getItemCount()) {
            throw new HtSemanticException("No such menu item.");
        }

        if (preposition == Preposition.INTO) {
            menu.remove(itemIndex);
        }

        addValueToMenu(value, menu, itemIndex);
    }

    /**
     * Puts a Value into a menu as a whole. See {@link #addValueToMenu(Value, JMenu, int)}
     *
     * @param value The value representing new menu items
     * @param preposition The preposition representing where items should be added
     * @throws HtSemanticException Thrown if an error occurs adding items.
     */
    private void putMenuValue(Value value, Preposition preposition) throws HtSemanticException {
        JMenu menu = this.menu.getSpecifiedMenu();

        switch (preposition) {
            case BEFORE:
                addValueToMenu(value, menu, 0);
                break;
            case AFTER:
                addValueToMenu(value, menu, menu.getItemCount());
                break;
            case INTO:
                menu.removeAll();
                addValueToMenu(value, menu, 0);
                break;
        }
    }

    /**
     * Places a value into a menu at a given menu item index. Value is interpreted as a list of items or lines with each
     * element in the list being added as a new menu item.
     *
     * @param v The value representing new menu items
     * @param menu The menu into which items should be added
     * @param index The index at which the menu items should be added.
     */
    private void addValueToMenu(Value v, JMenu menu, int index) {
        List<Value> menuItems = v.getLines();

        // If value contains a single line, then attempt to evaluate it as items
        if (menuItems.size() == 1) {
            menuItems = menuItems.get(0).getItems();
        }

        Collections.reverse(menuItems);

        for (Value thisItem : menuItems) {
            if (thisItem.stringValue().equals("-")) {
                menu.add(new JSeparator(), index);
            } else {
                MenuItemBuilder.ofDefaultType()
                        .named(thisItem.stringValue())
                        .atIndex(index)
                        .withAction(e -> {})        // No-op action
                        .build(menu);
            }
        }
    }

}
