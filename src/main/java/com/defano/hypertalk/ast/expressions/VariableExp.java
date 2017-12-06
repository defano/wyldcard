package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import org.antlr.v4.runtime.ParserRuleContext;

public class VariableExp extends Expression {

    private final String identifier;
    
    public VariableExp(ParserRuleContext context, String identifier) {
        super(context);
        this.identifier = identifier;
    }
    
    public Value onEvaluate() {
        return ExecutionContext.getContext().getVariable(identifier);
    }
}
