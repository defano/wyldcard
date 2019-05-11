package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.menu.main.WyldCardMenuBar;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.util.List;

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
        List<JMenu> visibleMenus = wyldCardMenuBar.getVisibleMenus();

        for (int menuIndex = 0; menuIndex < visibleMenus.size(); menuIndex++) {
            builder.append(visibleMenus.get(menuIndex).getText());

            if (menuIndex < visibleMenus.size() - 1) {
                builder.append("\n");
            }
        }

        return new Value(builder.toString());
    }
}
