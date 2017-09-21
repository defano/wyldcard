package com.defano.hypertalk.comparator;

import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;

import java.util.Comparator;
import java.util.HashMap;

public class CardExpressionComparator implements Comparator<CardModel> {

    private final Expression expression;
    private final SortStyle sortStyle;
    private final StackModel stack;
    private final SortDirection direction;

    private HashMap<CardModel, CardPart> cache = new HashMap<>();

    public CardExpressionComparator(StackModel stack, Expression expression, SortStyle sortStyle, SortDirection direction) {
        this.expression = expression;
        this.sortStyle = sortStyle;
        this.stack = stack;
        this.direction = direction;
    }

    @Override
    public int compare(CardModel o1, CardModel o2) {
        try {
            // Evaluate expression in the context of card o1
            ExecutionContext.getContext().setCurrentCard(acquire(o1));
            Value o1Value = expression.evaluate();

            // Evaluate expression in the context of card o2
            ExecutionContext.getContext().setCurrentCard(acquire(o2));
            Value o2Value = expression.evaluate();

            // Stop overriding card context in this thread
            ExecutionContext.getContext().setCurrentCard(null);

            if (direction == SortDirection.ASCENDING) {
                return o1Value.compareTo(o2Value, sortStyle);
            } else {
                return o2Value.compareTo(o1Value, sortStyle);
            }

        } catch (HtException e) {
            throw new HtUncheckedSemanticException(e);
        }
    }

    private CardPart acquire(CardModel model) throws HtException {
        if (!cache.containsKey(model)) {
            cache.put(model, CardPart.skeletonFromModel(model, stack));
        }

        return cache.get(model);
    }
}
