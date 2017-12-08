package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartContainerExp;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartReferenceExp extends PartContainerExp {

    private final Expression partExpression;

    public PartReferenceExp (ParserRuleContext context, Expression partExpression) {
        super(context);
        this.partExpression = partExpression;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtException {
        return dereference().evaluateAsSpecifier();
    }

    @Override
    public Value onEvaluate() throws HtException {
        return dereference().evaluate();
    }

    private PartContainerExp dereference() throws HtException {
        Value evaluated = partExpression.evaluate();
        Expression expression = Interpreter.evaluate(evaluated, PartContainerExp.class);

        if (expression == null) {
            throw new HtSemanticException("Expected a part, but got " + evaluated.stringValue());
        }

        return (PartContainerExp) expression;
    }
}
