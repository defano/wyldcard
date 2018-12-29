package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.menubar.main.WyldCardMenuBar;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Implementation of a HyperTalk function that returns the number of menus in the menu bar.
 */
public class MenusFunc extends Expression {

    @Inject
    private WyldCardMenuBar wyldCardMenuBar;

    public MenusFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) {
        StringBuilder builder = new StringBuilder();
        for (int menuIndex = 0; menuIndex < wyldCardMenuBar.getMenuCount(); menuIndex++) {
            builder.append(wyldCardMenuBar.getMenu(menuIndex).getText());

            if (menuIndex < wyldCardMenuBar.getMenuCount() - 1) {
                builder.append("\n");
            }
        }

        return new Value(builder.toString());
    }
}
