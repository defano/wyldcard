package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.gui.menu.MenuItemBuilder;
import com.defano.hypertalk.ast.common.MenuItemSpecifier;
import com.defano.hypertalk.ast.common.MenuSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContainerMenu extends Container {

    private final MenuSpecifier menu;
    private final MenuItemSpecifier item;

    public ContainerMenu(MenuItemSpecifier item) {
        this.item = item;
        this.menu = null;
    }

    public ContainerMenu(MenuSpecifier menu) {
        this.menu = menu;
        this.item = null;
    }

    @Override
    public Value getValue() throws HtSemanticException {
        return item != null ? getMenuItemValue() : getMenuValue();
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        if (item != null) {
            putMenuItemValue(value, preposition);
        } else {
            putMenuValue(value, preposition);
        }
    }

    private Value getMenuValue() throws HtSemanticException {
        JMenu menu = this.menu.getSpecifiedMenu();

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

    private Value getMenuItemValue() throws HtSemanticException {
        JMenu menu = item.getSpecifiedMenu();
        int itemIndex = item.getSpecifiedItemIndex();

        if (menu.getItem(itemIndex) == null) {
            return new Value("-");
        } else {
            return new Value(menu.getItem(itemIndex).getText());
        }
    }

    private void putMenuItemValue(Value value, Preposition preposition) throws HtSemanticException {
        JMenu menu = item.getSpecifiedMenu();
        int itemIndex = item.getSpecifiedItemIndex();

        switch (preposition) {
            case BEFORE:
                putIntoMenu(value, menu, itemIndex);
                break;
            case AFTER:
                putIntoMenu(value, menu, itemIndex + 1 <= menu.getItemCount() ? itemIndex + 1 : menu.getItemCount());
                break;
            case INTO:
                menu.remove(itemIndex);
                putIntoMenu(value, menu, itemIndex);
                break;
        }
    }

    private void putMenuValue(Value value, Preposition preposition) throws HtSemanticException {
        JMenu menu = this.menu.getSpecifiedMenu();

        switch (preposition) {
            case BEFORE:
                putIntoMenu(value, menu, 0);
                break;
            case AFTER:
                putIntoMenu(value, menu, menu.getItemCount());
                break;
            case INTO:
                menu.removeAll();
                putIntoMenu(value, menu, 0);
                break;
        }
    }

    private void putIntoMenu(Value v, JMenu menu, int startingIndex) {
        List<Value> menuItems = v.getLines();

        // If value contains a single line, then attempt to evaluate it as items
        if (menuItems.size() == 1) {
            menuItems = menuItems.get(0).getItems();
        }

        Collections.reverse(menuItems);
        for (Value thisItem : menuItems) {
            if (thisItem.stringValue().equals("-")) {
                menu.add(new JSeparator(), startingIndex);
            } else {
                MenuItemBuilder.ofDefaultType()
                        .named(thisItem.stringValue())
                        .atIndex(startingIndex)
                        .withAction(e -> {})        // No-op action
                        .build(menu);
            }
        }
    }

}
