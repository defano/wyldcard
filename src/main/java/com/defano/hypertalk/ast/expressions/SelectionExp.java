package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.SelectionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SelectionExp extends Expression {

    public SelectionExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        return SelectionContext.getInstance().getSelection();
    }
}
