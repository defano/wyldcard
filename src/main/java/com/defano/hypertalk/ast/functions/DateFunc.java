package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.DateLength;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.DateUtils;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Date;

public class DateFunc extends Expression {

    private final DateLength dateLength;

    public DateFunc(ParserRuleContext context, DateLength dateLength) {
        super(context);
        this.dateLength = dateLength;
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        return DateUtils.valueOf(new Date(), dateLength);
    }

}
