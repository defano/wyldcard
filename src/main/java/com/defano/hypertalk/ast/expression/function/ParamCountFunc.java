package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ParamCountFunc extends Expression {

    public ParamCountFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) {
        try {
            return new Value(context.getStackFrame().getParams().size());

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return new Value();
    }
}
