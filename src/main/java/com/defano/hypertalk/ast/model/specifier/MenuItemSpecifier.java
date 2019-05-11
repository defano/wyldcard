package com.defano.hypertalk.ast.model.specifier;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.enums.Ordinal;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;

import javax.swing.*;
import java.util.Random;

public class MenuItemSpecifier {

    private final MenuSpecifier menu;
    private final Ordinal ordinal;
    private final Expression expression;

    public MenuItemSpecifier(Expression expression, MenuSpecifier menu) {
        this.expression = expression;
        this.menu = menu;
        this.ordinal = null;
    }

    public MenuItemSpecifier(Ordinal ordinal, MenuSpecifier menu) {
        this.menu = menu;
        this.ordinal = ordinal;
        this.expression = null;
    }

    public boolean exists(ExecutionContext context) {
        try {
            getSpecifiedMenuItem(context);
            return true;
        } catch (HtException e) {
            return false;
        }
    }

    public JMenuItem getSpecifiedMenuItem(ExecutionContext context) throws HtException {
        return Invoke.onDispatch(() -> {
            JMenu menu = getSpecifiedMenu(context);
            int itemIndex = getSpecifiedItemIndex(context);

            if (itemIndex >= 0 && itemIndex < menu.getItemCount()) {
                JMenuItem foundItem = menu.getItem(itemIndex);
                if (foundItem != null) {
                    return foundItem;
                } else {
                    return new JMenuItem("-");
                }
            }

            throw new HtSemanticException("No such menu item.");
        }, HtException.class);
    }

    public JMenu getSpecifiedMenu(ExecutionContext context) throws HtException {
        return Invoke.onDispatch(() -> this.menu.getSpecifiedMenu(context), HtException.class);
    }

    public int getSpecifiedItemIndex(ExecutionContext context) throws HtException {
        return Invoke.onDispatch(() -> {
            JMenu menu = getSpecifiedMenu(context);

            if (expression != null) {
                Value exprValue = expression.evaluate(context);
                if (exprValue.isInteger()) {
                    return exprValue.integerValue() - 1;
                } else {
                    for (int index = 0; index < menu.getItemCount(); index++) {
                        JMenuItem thisItem = menu.getItem(index);

                        if (isSeparator(thisItem) && exprValue.toString().equals("-")) {
                            return index;
                        } else if (!isSeparator(thisItem) && exprValue.toString().equalsIgnoreCase(thisItem.getText())) {
                            return index;
                        }
                    }

                    throw new HtSemanticException("No such menu item " + exprValue.toString() + " in menu " + menu.getText());
                }
            } else if (ordinal != null) {
                if (menu.getItemCount() == 0) {
                    throw new HtSemanticException("There are no menu items.");
                }

                switch (ordinal) {
                    case LAST:
                        return menu.getItemCount() - 1;
                    case MIDDLE:
                        return menu.getItemCount() / 2;
                    case ANY:
                        return new Random().nextInt(menu.getItemCount());
                    default:
                        return ordinal.intValue() - 1;
                }
            }

            throw new HtSemanticException("No such menu item.");
        }, HtException.class);
    }

    private boolean isSeparator(JMenuItem item) {
        return Invoke.onDispatch(() -> item == null || item.getText() == null);
    }
}
