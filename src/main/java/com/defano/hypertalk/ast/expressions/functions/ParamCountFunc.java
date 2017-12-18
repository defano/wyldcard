package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ParamCountFunc extends Expression {

    public ParamCountFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        try {
            return new Value(ExecutionContext.getContext().getParams().size());

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return new Value();
    }
}
