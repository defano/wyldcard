package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.CompilationUnit;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.MenuContainer;
import com.defano.hypertalk.ast.specifiers.MenuSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class MenuExp extends Expression {

    private final MenuSpecifier menuSpecifier;
    private final Expression expression;

    public MenuExp(ParserRuleContext context, MenuSpecifier menuContainer) {
        super(context);
        this.menuSpecifier = menuContainer;
        this.expression = null;
    }

    public MenuExp(ParserRuleContext context, Expression expression) {
        super(context);
        this.menuSpecifier = null;
        this.expression = expression;
    }

    @Override
    public Value onEvaluate() throws HtException {
        MenuSpecifier specifier = evaluateAsMenuSpecifier();
        if (specifier != null) {
            return new MenuContainer(specifier).getValue();
        } else {
            throw new HtSemanticException("Not a menu item.");
        }
    }

    public MenuSpecifier evaluateAsMenuSpecifier() throws HtException {
        if (menuSpecifier != null) {
            return menuSpecifier;
        } else {
            MenuExp exp = Interpreter.evaluate(CompilationUnit.MENU_EXPRESSION, expression, MenuExp.class);
            return exp == null ? null : exp.menuSpecifier;
        }
    }

}
