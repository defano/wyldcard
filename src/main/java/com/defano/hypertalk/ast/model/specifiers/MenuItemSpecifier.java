package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.model.Ordinal;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;

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

    public JMenuItem getSpecifiedMenuItem(ExecutionContext context) throws HtException {
        JMenu menu = getSpecifiedMenu(context);
        int itemIndex = getSpecifiedItemIndex(context);

        if (itemIndex >= 0 && itemIndex < menu.getItemCount()) {
            return menu.getItem(itemIndex);
        }

        throw new HtSemanticException("No such menu item.");
    }

    public JMenu getSpecifiedMenu(ExecutionContext context) throws HtException {
        return this.menu.getSpecifiedMenu(context);
    }

    public int getSpecifiedItemIndex(ExecutionContext context) throws HtException {
        JMenu menu = getSpecifiedMenu(context);

        if (expression != null) {
            Value exprValue = expression.evaluate(context);
            if (exprValue.isInteger()) {
                return exprValue.integerValue() - 1;
            } else {
                for (int index = 0; index < menu.getItemCount(); index++) {
                    JMenuItem thisItem = menu.getItem(index);

                    if (thisItem == null || thisItem.getText() == null && exprValue.toString().equals("-")) {
                        return index;
                    } else if (exprValue.toString().equalsIgnoreCase(thisItem.getText())) {
                        return index;
                    }
                }

                throw new HtSemanticException("No such menu item " + exprValue.toString() + " in menu " + menu.getText());
            }
        }

        else if (ordinal != null) {
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
    }
}
