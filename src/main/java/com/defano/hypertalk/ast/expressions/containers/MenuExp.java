package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypercard.menu.MenuItemBuilder;
import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.MenuSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuExp extends ContainerExp {

    public final MenuSpecifier menu;

    public MenuExp(ParserRuleContext context, MenuSpecifier menu) {
        super(context);
        this.menu = menu;
    }

    @Override
    public Value onEvaluate() throws HtException {
        Value evaluated = getMenuValue(menu.getSpecifiedMenu());
        return chunkOf(evaluated, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        putMenuValue(value, preposition);
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
     * Puts a Value into a menu as a whole. See {@link #addValueToMenu(Value, JMenu, int)}
     *
     * @param value The value representing new menu items
     * @param preposition The preposition representing where items should be added
     * @throws HtSemanticException Thrown if an error occurs adding items.
     */
    private void putMenuValue(Value value, Preposition preposition) throws HtException {
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
    public static void addValueToMenu(Value v, JMenu menu, int index) {
        List<Value> menuItems = v.getLines();

        // If value contains a single line, then attempt to onEvaluate it as items
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
