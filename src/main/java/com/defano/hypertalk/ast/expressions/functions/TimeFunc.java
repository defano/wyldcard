package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.enums.ConvertibleDateFormat;
import com.defano.hypertalk.ast.model.enums.LengthAdjective;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Date;

public class TimeFunc extends Expression {

    private final LengthAdjective lengthAdjective;

    public TimeFunc(ParserRuleContext context, LengthAdjective lengthAdjective) {
        super(context);
        this.lengthAdjective = lengthAdjective;
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtSemanticException {
        switch (lengthAdjective) {
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
