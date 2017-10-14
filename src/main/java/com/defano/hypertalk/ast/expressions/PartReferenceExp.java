package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartReferenceExp extends PartExp {

    private final String symbol;

    public PartReferenceExp (ParserRuleContext context, String symbol) {
        super(context);
        this.symbol = symbol;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtException {
        return dereference().evaluateAsSpecifier();
    }

    @Override
    public Value onEvaluate() throws HtException {
        return dereference().evaluate();
    }

    private PartExp dereference() throws HtSemanticException {
        Value value = ExecutionContext.getContext().getVariable(symbol);
        Expression expression = Interpreter.dereference(value, PartExp.class);

        if (expression == null) {
            throw new HtSemanticException("Expected a part, but got " + value);
        }

        return (PartExp) expression;
    }
}
