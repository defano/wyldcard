package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.ConvertibleDateFormat;
import com.defano.hypertalk.ast.model.Adjective;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Date;

public class TimeFunc extends Expression {

    private final Adjective adjective;

    public TimeFunc(ParserRuleContext context, Adjective adjective) {
        super(context);
        this.adjective = adjective;
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtSemanticException {
        switch (adjective) {
            case LONG:
                return new Value(ConvertibleDateFormat.LONG_TIME.dateFormat.format(new Date()));
            case SHORT:
            case ABBREVIATED:
                return new Value(ConvertibleDateFormat.SHORT_TIME.dateFormat.format(new Date()));
            default:
                throw new HtSemanticException("Bug! Unimplemented time format.");
        }
    }
}
