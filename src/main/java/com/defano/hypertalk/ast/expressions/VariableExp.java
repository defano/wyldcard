package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class VariableExp extends PartExp {

    private final String identifier;
    
    public VariableExp(ParserRuleContext context, String identifier) {
        super(context);
        this.identifier = identifier;
    }
    
    public Value onEvaluate() {
        return ExecutionContext.getContext().getVariable(identifier);
    }

    @Override
    public PartSpecifier evaluateAsSpecifier() throws HtException {
        return dereference().evaluateAsSpecifier();
    }

    private PartExp dereference() throws HtSemanticException {
        Value value = ExecutionContext.getContext().getVariable(identifier);
        Expression expression = Interpreter.dereference(value, PartExp.class);

        if (expression == null) {
            throw new HtSemanticException("Expected a part, but got " + value);
        }

        return (PartExp) expression;
    }
}
