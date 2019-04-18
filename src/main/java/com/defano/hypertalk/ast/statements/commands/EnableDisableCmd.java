package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypertalk.ast.expressions.factor.FactorAssociation;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.containers.MenuExp;
import com.defano.hypertalk.ast.expressions.containers.MenuItemExp;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.expressions.*;
import com.defano.hypertalk.ast.model.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;

public class EnableDisableCmd extends Command {

    private final Expression expression;
    private final boolean enable;

    public EnableDisableCmd(ParserRuleContext context, Expression expression, boolean enable) {
        super(context, "disable");
        this.expression = expression;
        this.enable = enable;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException {
        boolean success = expression.factor(
                context, new FactorAssociation<>(MenuItemExp.class, menuItemExp -> disableMenuItem(context, menuItemExp)),
                new FactorAssociation<>(MenuExp.class, menuExp -> disableMenu(context, menuExp)),
                new FactorAssociation<>(PartExp.class, partExp -> disablePart(partExp, context))
        );

        if (!success) {
            throw new HtSemanticException("Can't " + (enable ? "enable" : "disable") + " that.");
        }
    }

    private void disablePart(PartExp partExp, ExecutionContext context) throws HtException {
        PartModel model = context.getPart(partExp.evaluateAsSpecifier(context));
        model.trySet(context, CardLayerPartModel.PROP_ENABLED, new Value(enable));
    }

    private void disableMenuItem(ExecutionContext context, MenuItemExp menuItemExp) throws HtException {
        MenuItemSpecifier specifier = menuItemExp.item;
        JMenu theMenu = specifier.getSpecifiedMenu(context);
        int menuItemIndex = specifier.getSpecifiedItemIndex(context);

        if (menuItemIndex < 0 || menuItemIndex > theMenu.getItemCount()) {
            throw new HtSemanticException("No such menu item in menu " + theMenu.getText());
        }

        theMenu.getItem(menuItemIndex).setEnabled(enable);
    }

    private void disableMenu(ExecutionContext context, MenuExp menuExp) throws HtException {
        JMenu theMenu = menuExp.menu.getSpecifiedMenu(context);
        theMenu.setEnabled(enable);
    }
}
