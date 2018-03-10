package com.defano.hypertalk.ast.expressions.functions;

import com.defano.wyldcard.parts.finder.LayeredPartFinder;
import com.defano.wyldcard.parts.finder.PartFinder;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class NumberOfPartFunc extends Expression {

    private final Expression partExpression;

    public NumberOfPartFunc(ParserRuleContext context, Expression partExpression) {
        super(context);
        this.partExpression = partExpression;
    }

    @Override
    protected Value onEvaluate() throws HtException {
        PartExp partFactor = partExpression.factor(PartExp.class, new HtSemanticException("Don't know how to get the number of that."));

        PartModel part = ExecutionContext.getContext().getPart(partFactor.evaluateAsSpecifier());

        if (part instanceof ButtonModel) {
            return new Value(((LayeredPartFinder) part.getParentPartModel()).getPartNumber(part, PartType.BUTTON));
        }

        if (part instanceof FieldModel) {
            return new Value(((LayeredPartFinder) part.getParentPartModel()).getPartNumber(part, PartType.FIELD));
        }

        if (part instanceof CardModel) {
            return new Value(((PartFinder) part.getParentPartModel()).getPartNumber(part, PartType.CARD));
        }

        if (part instanceof BackgroundModel) {
            return new Value(((PartFinder) part.getParentPartModel()).getPartNumber(part, PartType.BACKGROUND));
        }

        throw new HtSemanticException("Don't know how to get the number of that.");
    }
}
