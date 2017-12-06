package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.CompilationUnit;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.MenuExp;
import com.defano.hypertalk.ast.expressions.MenuItemExp;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.ast.specifiers.MenuSpecifier;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;

public class DisableCmd extends Command {

    private final Expression expression;

    public DisableCmd(ParserRuleContext context, Expression expression) {
        super(context, "disable");
        this.expression = expression;
    }

    @Override
    protected void onExecute() throws HtException {
        if (! (attemptDisablePart() || attemptDisableMenu() || attemptDisableMenuItem())) {
            throw new HtSemanticException("Can't disable that.");
        }
    }

    private boolean attemptDisablePart() {
        try {
            PartExp partExp = expression.evaluateAsPart();
            if (partExp != null) {
                disablePart(partExp);
            }
        } catch (HtException e) {
            return false;
        }

        return true;
    }

    private boolean attemptDisableMenuItem() {
        try {
            MenuItemExp menuItemExp = Interpreter.evaluate(CompilationUnit.MENUITEM_EXPRESSION, expression, MenuItemExp.class);
            if (menuItemExp != null) {
                MenuItemSpecifier menuItemSpecifier = menuItemExp.evaluateAsMenuItemSpecifier();
                if (menuItemSpecifier != null) {
                    disableMenuItem(menuItemExp);
                }
            }
        } catch (HtException e) {
            return false;
        }

        return true;
    }

    private boolean attemptDisableMenu() {
        try {
            MenuExp menuExp = Interpreter.evaluate(CompilationUnit.MENU_EXPRESSION, expression, MenuExp.class);
            if (menuExp != null) {
                MenuSpecifier menuSpecifier = menuExp.evaluateAsMenuSpecifier();
                if (menuSpecifier != null) {
                    disableMenu(menuExp);
                }
            }
        } catch (HtException e) {
            return false;
        }

        return true;
    }

    private void disablePart(PartExp partExp) throws HtException {
        PartModel model = ExecutionContext.getContext().getPart(partExp.evaluateAsSpecifier());
        model.setProperty(CardLayerPartModel.PROP_ENABLED, new Value(false));
    }

    private void disableMenuItem(MenuItemExp menuItemExp) throws HtException {
        MenuItemSpecifier specifier = menuItemExp.evaluateAsMenuItemSpecifier();
        JMenu theMenu = specifier.getSpecifiedMenu();
        int menuItemIndex = specifier.getSpecifiedItemIndex();

        if (menuItemIndex < 0 || menuItemIndex > theMenu.getItemCount()) {
            throw new HtSemanticException("No such menu item in menu " + theMenu.getText());
        }

        theMenu.getItem(menuItemIndex).setEnabled(false);
    }

    private void disableMenu(MenuExp menuExp) throws HtException {
        JMenu theMenu = menuExp.evaluateAsMenuSpecifier().getSpecifiedMenu();
        theMenu.setEnabled(false);
    }
}
