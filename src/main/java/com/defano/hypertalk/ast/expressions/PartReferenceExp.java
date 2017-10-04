package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class PartReferenceExp extends PartExp {

    private final String symbol;

    public PartReferenceExp (String symbol) {
        this.symbol = symbol;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        return dereference().evaluateAsSpecifier();
    }

    @Override
    public Value evaluate() throws HtSemanticException {
        return dereference().evaluate();
    }

    private PartExp dereference() throws HtSemanticException {
        Value value = ExecutionContext.getContext().get(symbol);
        Expression expression = Interpreter.dereference(value, PartExp.class);

        if (expression == null) {
            throw new HtSemanticException("Expected a part, but got " + value);
        }

        return (PartExp) expression;
    }
}
