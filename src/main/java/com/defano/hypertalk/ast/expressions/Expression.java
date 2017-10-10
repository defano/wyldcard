package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;


public abstract class Expression extends ASTNode {

    public Expression(ParserRuleContext context) {
        super(context);
    }

    protected abstract Value onEvaluate() throws HtException;

    public Value evaluate() throws HtException {
        try {
            return onEvaluate();
        } catch (HtException e) {
            rethrowContextualizedException(e);
        }

        throw new IllegalStateException("Bug! Contextualized exception not thrown.");
    }
}
