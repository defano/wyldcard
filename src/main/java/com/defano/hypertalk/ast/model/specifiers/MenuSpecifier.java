package com.defano.hypertalk.ast.model.specifiers;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Ordinal;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;

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

    public boolean exists(ExecutionContext context) {
        try {
            getSpecifiedMenu(context);
            return true;
        } catch (HtException e) {
            return false;
        }
    }

    public JMenu getSpecifiedMenu(ExecutionContext context) throws HtException {
        return Invoke.onDispatch(() -> {
            if (menuExpr != null) {
                JMenu foundMenu;
                Value menuExprValue = menuExpr.evaluate(context);

                foundMenu = WyldCard.getInstance().getWyldCardMenuBar().findMenuByName(menuExprValue.toString());

                if (foundMenu == null) {
                    foundMenu = WyldCard.getInstance().getWyldCardMenuBar().findMenuByNumber(menuExprValue.integerValue() - 1);
                }

                if (foundMenu == null) {
                    throw new HtSemanticException("No such menu " + menuExprValue.toString());
                }

                return foundMenu;
            }

            if (menuOrdinal != null) {
                int menuCount = WyldCard.getInstance().getWyldCardMenuBar().getVisibleMenus().size();
                JMenu foundMenu;

                if (menuCount == 0) {
                    throw new HtSemanticException("There are no menus.");
                }

                switch (menuOrdinal) {
                    case LAST:
                        foundMenu = WyldCard.getInstance().getWyldCardMenuBar().findMenuByNumber(menuCount - 1);
                        break;
                    case MIDDLE:
                        foundMenu = WyldCard.getInstance().getWyldCardMenuBar().findMenuByNumber(menuCount / 2);
                        break;
                    case ANY:
                        foundMenu = WyldCard.getInstance().getWyldCardMenuBar().findMenuByNumber(new Random().nextInt(menuCount));
                        break;
                    default:
                        foundMenu = WyldCard.getInstance().getWyldCardMenuBar().findMenuByNumber(menuOrdinal.intValue() - 1);
                        break;
                }

                if (foundMenu == null) {
                    throw new HtSemanticException("No such menu number " + menuOrdinal.intValue());
                }

                return foundMenu;
            }

            throw new HtSemanticException("Can't find that menu.");
        }, HtException.class);
    }
}
