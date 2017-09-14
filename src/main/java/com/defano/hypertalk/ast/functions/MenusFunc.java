package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

/**
 * Implementation of a HyperTalk function that returns the number of menus in the menu bar.
 */
public class MenusFunc extends Expression {

    @Override
    public Value evaluate() throws HtSemanticException {
        StringBuilder builder = new StringBuilder();
        for (int menuIndex = 0; menuIndex < HyperCardMenuBar.instance.getMenuCount(); menuIndex++) {
            builder.append(HyperCardMenuBar.instance.getMenu(menuIndex).getText());

            if (menuIndex < HyperCardMenuBar.instance.getMenuCount() - 1) {
                builder.append("\n");
            }
        }

        return new Value(builder.toString());
    }
}
