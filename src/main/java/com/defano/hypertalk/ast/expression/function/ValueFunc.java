package com.defano.hypertalk.ast.expression.function;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.executor.ScriptExecutor;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ValueFunc extends Expression {

    public final Expression expression;

    public ValueFunc(ParserRuleContext context, Expression expression) {
        super(context);
        this.expression = expression;
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        String toEvaluate = expression.evaluate(context).toString();
        return ScriptExecutor.blockingEvaluate(toEvaluate, context);
    }
}
