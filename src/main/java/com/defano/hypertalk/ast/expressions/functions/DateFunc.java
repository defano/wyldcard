package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.ast.model.Adjective;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.DateUtils;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Date;

public class DateFunc extends Expression {

    private final Adjective adjective;

    public DateFunc(ParserRuleContext context, Adjective adjective) {
        super(context);
        this.adjective = adjective;
    }

    @Override
    public Value onEvaluate() throws HtSemanticException {
        return DateUtils.valueOf(new Date(), adjective);
    }

}
