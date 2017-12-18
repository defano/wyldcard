package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypercard.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.model.Ordinal;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;
import java.util.Random;

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

    public JMenu getSpecifiedMenu() throws HtException {

        if (menuExpr != null) {
            JMenu foundMenu;
            Value menuExprValue = menuExpr.evaluate();

            foundMenu = HyperCardMenuBar.instance.findMenuByName(menuExprValue.stringValue());

            if (foundMenu == null) {
                foundMenu = HyperCardMenuBar.instance.findMenuByNumber(menuExprValue.integerValue() - 1);
            }

            if (foundMenu == null) {
                throw new HtSemanticException("No such menu " + menuExprValue.stringValue());
            }

            return foundMenu;
        }

        if (menuOrdinal != null) {
            int menuCount = HyperCardMenuBar.instance.getMenuCount();
            JMenu foundMenu;

            if (menuCount == 0) {
                throw new HtSemanticException("There are no menus.");
            }

            switch (menuOrdinal) {
                case LAST:
                    foundMenu = HyperCardMenuBar.instance.findMenuByNumber(menuCount - 1);
                    break;
                case MIDDLE:
                    foundMenu = HyperCardMenuBar.instance.findMenuByNumber(menuCount / 2);
                    break;
                case ANY:
                    foundMenu = HyperCardMenuBar.instance.findMenuByNumber(new Random().nextInt(menuCount));
                    break;
                default:
                    foundMenu = HyperCardMenuBar.instance.findMenuByNumber(menuOrdinal.intValue() - 1);
                    break;
            }

            if (foundMenu == null) {
                throw new HtSemanticException("No such menu number " + menuOrdinal.intValue());
            }

            return foundMenu;
        }

        throw new HtSemanticException("Can't find that menu.");
    }
}
