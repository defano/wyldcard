package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DestinationReferenceExp extends DestinationExp {

    private final Expression expression;

    public DestinationReferenceExp(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtSemanticException {
        Value value = expression.onEvaluate();
        PartExp expression = Interpreter.dereference(value, PartExp.class);

        if (expression == null) {
            throw new HtSemanticException("Expected a destination, but got " + value);
        }

        return expression.evaluateAsSpecifier();
    }
}
