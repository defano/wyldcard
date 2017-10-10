package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class SecondsFunc extends Expression {

    public SecondsFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        return new Value(System.currentTimeMillis() / 1000);
    }
}
