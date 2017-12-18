package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import org.antlr.v4.runtime.ParserRuleContext;

public class ResultFunc extends Expression {

    public ResultFunc(ParserRuleContext context) {
        super(context);
    }
    
    public Value onEvaluate() {
        return ExecutionContext.getContext().getResult();
    }
}
