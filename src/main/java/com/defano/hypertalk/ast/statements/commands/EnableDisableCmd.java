package com.defano.hypertalk.ast.statements.commands;

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
    protected void onExecute() throws HtException {
        boolean success = expression.factor(
                new FactorAssociation<>(MenuItemExp.class, this::disableMenuItem),
                new FactorAssociation<>(MenuExp.class, this::disableMenu),
                new FactorAssociation<>(PartExp.class, this::disablePart)
        );

        if (!success) {
            throw new HtSemanticException("Can't " + (enable ? "enable" : "disable") + " that.");
        }
    }

    private void disablePart(PartExp partExp) throws HtException {
        PartModel model = ExecutionContext.getContext().getPart(partExp.evaluateAsSpecifier());
        model.setProperty(CardLayerPartModel.PROP_ENABLED, new Value(enable));
    }

    private void disableMenuItem(MenuItemExp menuItemExp) throws HtException {
        MenuItemSpecifier specifier = menuItemExp.item;
        JMenu theMenu = specifier.getSpecifiedMenu();
        int menuItemIndex = specifier.getSpecifiedItemIndex();

        if (menuItemIndex < 0 || menuItemIndex > theMenu.getItemCount()) {
            throw new HtSemanticException("No such menu item in menu " + theMenu.getText());
        }

        theMenu.getItem(menuItemIndex).setEnabled(enable);
    }

    private void disableMenu(MenuExp menuExp) throws HtException {
        JMenu theMenu = menuExp.menu.getSpecifiedMenu();
        theMenu.setEnabled(enable);
    }
}
