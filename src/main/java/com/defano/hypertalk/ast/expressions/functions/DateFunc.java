package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.enums.LengthAdjective;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.utils.DateUtils;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Date;

public class DateFunc extends Expression {

    private final LengthAdjective lengthAdjective;

    public DateFunc(ParserRuleContext context, LengthAdjective lengthAdjective) {
        super(context);
        this.lengthAdjective = lengthAdjective;
    }

    @Override
    public Value onEvaluate(ExecutionContext context) {
        return DateUtils.valueOf(new Date(), lengthAdjective);
    }

}
