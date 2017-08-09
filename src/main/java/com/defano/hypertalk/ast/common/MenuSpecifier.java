package com.defano.hypertalk.ast.common;

import com.defano.hypercard.gui.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;

public class MenuSpecifier {

    private final Expression menuExpr;
    private final Ordinal menuOrdinal;

    public MenuSpecifier(Expression menuExpr) {
        this.menuExpr = menuExpr;
        this.menuOrdinal = null;
    }

    public MenuSpecifier(Ordinal menuOrdinal) {
        this.menuOrdinal = menuOrdinal;
        this.menuExpr = null;
    }

    public JMenu getSpecifiedMenu() throws HtSemanticException {

        if (menuExpr != null) {
            JMenu foundMenu;
            Value menuExprValue = menuExpr.evaluate();

            foundMenu = HyperCardMenuBar.instance.findMenuByName(menuExprValue.stringValue());

            if (foundMenu == null) {
                foundMenu = HyperCardMenuBar.instance.findMenuByNumber(menuExprValue.integerValue() - 1);
            }

            return foundMenu;
        }

        if (menuOrdinal != null) {
            int menuCount = HyperCardMenuBar.instance.getMenuCount();

            switch (menuOrdinal) {
                case LAST:
                    return HyperCardMenuBar.instance.findMenuByNumber(menuCount - 1);
                case MIDDLE:
                    return HyperCardMenuBar.instance.findMenuByNumber(menuCount / 2);
                default:
                    return HyperCardMenuBar.instance.findMenuByNumber(menuOrdinal.intValue() - 1);
            }
        }

        throw new HtSemanticException("Can't find that menu.");
    }
}
