package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.menu.HyperCardMenuBar;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Implementation of a HyperTalk function that returns the number of menus in the menu bar.
 */
public class MenusFunc extends Expression {

    public MenusFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate() {
        StringBuilder builder = new StringBuilder();
        for (int menuIndex = 0; menuIndex < HyperCardMenuBar.getInstance().getMenuCount(); menuIndex++) {
            builder.append(HyperCardMenuBar.getInstance().getMenu(menuIndex).getText());

            if (menuIndex < HyperCardMenuBar.getInstance().getMenuCount() - 1) {
                builder.append("\n");
            }
        }

        return new Value(builder.toString());
    }
}
