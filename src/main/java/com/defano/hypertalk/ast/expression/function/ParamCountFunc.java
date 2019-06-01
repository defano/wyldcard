package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParamCountFunc extends Expression {

    private static final Logger LOG = LoggerFactory.getLogger(ParamCountFunc.class);

    public ParamCountFunc(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) {
        try {
            return new Value(context.getStackFrame().getParams().size());
        } catch (Exception e) {
            LOG.error("Bug! Caught exception while calculating param count.", e);
        }

        return new Value();
    }
}
