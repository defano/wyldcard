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

    public final MenuSpecifier menuSpecifier;

    public MenuExp(ParserRuleContext context, MenuSpecifier menuContainer) {
        super(context);
        this.menuSpecifier = menuContainer;
    }

    @Override
    public Value onEvaluate() throws HtException {
        return new MenuContainer(menuSpecifier).getValue();
    }
}
