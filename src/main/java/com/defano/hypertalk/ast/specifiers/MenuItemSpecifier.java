package com.defano.hypertalk.ast.specifiers;

import com.defano.hypertalk.ast.common.Ordinal;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;

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

    public JMenuItem getSpecifiedMenuItem() throws HtException {
        JMenu menu = getSpecifiedMenu();
        int itemIndex = getSpecifiedItemIndex();

        if (itemIndex >= 0 && itemIndex < menu.getItemCount()) {
            return menu.getItem(itemIndex);
        }

        throw new HtSemanticException("No such menu item.");
    }

    public JMenu getSpecifiedMenu() throws HtException {
        return this.menu.getSpecifiedMenu();
    }

    public int getSpecifiedItemIndex() throws HtException {
        JMenu menu = getSpecifiedMenu();

        if (expression != null) {
            Value exprValue = expression.evaluate();
            if (exprValue.isInteger()) {
                return exprValue.integerValue() - 1;
            } else {
                for (int index = 0; index < menu.getItemCount(); index++) {
                    JMenuItem thisItem = menu.getItem(index);

                    if (thisItem == null || thisItem.getText() == null && exprValue.stringValue().equals("-")) {
                        return index;
                    } else if (exprValue.stringValue().equalsIgnoreCase(thisItem.getText())) {
                        return index;
                    }
                }

                throw new HtSemanticException("No such menu item " + exprValue.stringValue() + " in menu " + menu.getText());
            }
        }

        else if (ordinal != null) {
            switch (ordinal) {
                case LAST:
                    return menu.getItemCount() - 1;
                case MIDDLE:
                    return menu.getItemCount() / 2;
                default:
                    return ordinal.intValue() - 1;
            }
        }

        throw new HtSemanticException("No such menu item.");
    }
}
