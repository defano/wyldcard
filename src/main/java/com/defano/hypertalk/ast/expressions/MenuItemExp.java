package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.CompilationUnit;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.MenuContainer;
import com.defano.hypertalk.ast.specifiers.MenuItemSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class MenuItemExp extends Expression {

    private final MenuItemSpecifier menuItemSpecifier;
    private final Expression expression;

    public MenuItemExp(ParserRuleContext context, MenuItemSpecifier menuItemSpecifier) {
        super(context);
        this.menuItemSpecifier = menuItemSpecifier;
        this.expression = null;
    }

    public MenuItemExp(ParserRuleContext context, Expression expression) {
        super(context);
        this.menuItemSpecifier = null;
        this.expression = expression;
    }

    @Override
    protected Value onEvaluate() throws HtException {
        MenuItemSpecifier specifier = evaluateAsMenuItemSpecifier();
        if (specifier != null) {
            return new MenuContainer(specifier).getValue();
        } else {
            throw new HtSemanticException("Not a menu item.");
        }
    }

    public MenuItemSpecifier evaluateAsMenuItemSpecifier() throws HtException {
        if (menuItemSpecifier != null) {
            return menuItemSpecifier;
        } else {
            MenuItemExp exp = Interpreter.evaluate(CompilationUnit.MENUITEM_EXPRESSION, expression, MenuItemExp.class);
            return exp == null ? null : exp.menuItemSpecifier;
        }
    }

}
