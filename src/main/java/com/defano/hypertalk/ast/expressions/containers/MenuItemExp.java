package com.defano.hypertalk.ast.expressions.containers;

import com.defano.hypertalk.ast.model.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemExp extends ContainerExp {

    public final MenuItemSpecifier item;

    public MenuItemExp(ParserRuleContext context, MenuItemSpecifier item) {
        super(context);
        this.item = item;
    }

    @Override
    public void putValue(ExecutionContext context, Value value, Preposition preposition) throws HtException {
        putMenuItemValue(context, value, preposition, new ArrayList<>());
    }

    public void putValue(ExecutionContext context, Value value, Preposition preposition, List<Value> menuMessages) throws HtException {
        putMenuItemValue(context, value, preposition, menuMessages);
    }

    @Override
    protected Value onEvaluate(ExecutionContext context) throws HtException {
        Value evaluated = getMenuItemValue(item.getSpecifiedMenu(context), item.getSpecifiedItemIndex(context));
        return chunkOf(context, evaluated, getChunk());
    }

    /**
     * Gets the value of a specific menu item.
     *
     * @param itemIndex The index of the menu item whose value should be returned.
     * @param menu The menu whose menu items should be returned.
     * @return The value of the specified menu item.
     */
    private Value getMenuItemValue(JMenu menu, int itemIndex) throws HtSemanticException {
        return Invoke.onDispatch(() -> {
            if (itemIndex < 0 || itemIndex >= menu.getItemCount()) {
                throw new HtSemanticException("No such menu item " + (itemIndex + 1));
            }

            if (menu.getItem(itemIndex) == null) {
                return new Value("-");
            } else {
                return new Value(menu.getItem(itemIndex).getText());
            }
        }, HtSemanticException.class);
    }

    /**
     * Puts a Value into a menu relative to a given menu item. See {@link MenuExp#addValueToMenu(ExecutionContext, Value, JMenu, int)}
     *
     * @param context The execution context.
     * @param value the value representing new menu items.
     * @param preposition The preposition representing where items should be added relative to the given menu item.
     * @throws HtSemanticException Thrown if an error occurs adding items.
     */
    private void putMenuItemValue(ExecutionContext context, Value value, Preposition preposition, List<Value> menuMessages) throws HtException {
        Invoke.onDispatch(() -> {
            JMenu menu = item.getSpecifiedMenu(context);
            int itemIndex = item.getSpecifiedItemIndex(context);       // Location of specified item

            if (itemIndex < 0 || itemIndex > menu.getItemCount()) {
                throw new HtSemanticException("No such menu item.");
            }

            switch (preposition) {
                case BEFORE:
                    itemIndex--;
                    break;
                case AFTER:
                    itemIndex++;
                    break;
                case INTO:
                case REPLACING:
                    menu.remove(itemIndex);
                    break;
            }

            MenuExp.addValueToMenu(context, value, menu, itemIndex, menuMessages);

        }, HtException.class);
    }
}
