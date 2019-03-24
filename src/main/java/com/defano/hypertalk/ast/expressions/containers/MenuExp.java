package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.MenuSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.runtime.context.ExecutionContext;
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
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Value evaluated = getMenuValue(menu.getSpecifiedMenu(context));
        return chunkOf(context, evaluated, getChunk());
    }

    @Override
    public void putValue(ExecutionContext context, Value value, Preposition preposition) throws HtException {
        putMenuValue(context, value, preposition, new ArrayList<>());
    }

    public void putValue(ExecutionContext context, Value value, Preposition preposition, List<Value> menuMessages) throws HtException {
        putMenuValue(context, value, preposition, menuMessages);
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
     * Puts a Value into a menu as a whole. See {@link #addValueToMenu(ExecutionContext, Value, JMenu, int)}
     *
     *
     * @param context The execution context.
     * @param value The value representing new menu items
     * @param preposition The preposition representing where items should be added
     * @throws HtSemanticException Thrown if an error occurs adding items.
     */
    private void putMenuValue(ExecutionContext context, Value value, Preposition preposition, List<Value> menuMessages) throws HtException {
        JMenu menu = this.menu.getSpecifiedMenu(context);

        switch (preposition) {
            case BEFORE:
                addValueToMenu(context, value, menu, 0, menuMessages);
                break;
            case AFTER:
                addValueToMenu(context, value, menu, menu.getItemCount(), menuMessages);
                break;
            case INTO:
                menu.removeAll();
                addValueToMenu(context, value, menu, 0, menuMessages);
                break;
        }
    }

    /**
     * Places a value into a menu at a given menu item index. Value is interpreted as a list of items or lines with each
     * element in the list being added as a new menu item.
     *
     * @param context The execution context.
     * @param v The value representing new menu items
     * @param menu The menu into which items should be added
     * @param index The index at which the menu items should be added.
     */
    public static void addValueToMenu(ExecutionContext context, Value v, JMenu menu, int index) throws HtSemanticException {
        addValueToMenu(context, v, menu, index, new ArrayList<>());
    }

    public static void addValueToMenu(ExecutionContext context, Value v, JMenu menu, int index, List<Value> menuMessages) throws HtSemanticException {
        List<Value> menuItems = v.getLines(context);

        // If value contains a single line, then attempt to onEvaluate it as items
        if (menuItems.size() == 1) {
            menuItems = menuItems.get(0).getItems(context);
        }

        Collections.reverse(menuItems);
        Collections.reverse(menuMessages);

        if (!menuMessages.isEmpty() && menuItems.size() != menuMessages.size()) {
            throw new HtSemanticException("The number of menu messages does not equal the number of menu items.");
        }

        for (int idx = 0; idx < menuItems.size(); idx++) {
            Value thisItem = menuItems.get(idx);

            if (thisItem.toString().equals("-")) {
                menu.add(new JSeparator(), index);
            } else {
                int messageIdx = idx;
                MenuItemBuilder.ofDefaultType()
                        .named(thisItem.toString())
                        .atIndex(index)
                        .withAction(e -> {
                            if (!menuMessages.isEmpty()) {
                                context.getCurrentCard().getPartModel().receiveMessage(context, menuMessages.get(messageIdx).toString());
                            }
                        })
                        .build(menu);
            }
        }
    }

}
