package com.defano.hypertalk.ast.expressions;

import com.defano.wyldcard.parts.model.PartModel;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExistenceExp extends Expression {

    private final boolean existsPolarity;
    private final Expression expression;

    public ExistenceExp(ParserRuleContext context, Expression expression, String existenceOperator) {
        super(context);

        this.expression = expression;
        this.existsPolarity = !existenceOperator.toLowerCase().contains("no");
    }

    @Override
    protected Value onEvaluate() throws HtException {
        PartModel object = expression.partFactor(PartModel.class);
        return new Value((object != null) == existsPolarity);
    }
}
