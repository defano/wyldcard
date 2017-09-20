package com.defano.hypertalk.comparator;

import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

import java.util.Comparator;
import java.util.HashMap;

public class CardExpressionComparator implements Comparator<CardModel> {

    private final Expression expression;
    private final SortStyle sortStyle;
    private final StackModel model;

    private HashMap<CardModel,CardPart> cache = new HashMap<>();

    public CardExpressionComparator(StackModel model, Expression expression, SortStyle sortStyle) {
        this.expression = expression;
        this.sortStyle = sortStyle;
        this.model = model;
    }

    @Override
    public int compare(CardModel o1, CardModel o2) {
        try {
            if (cache.get(o1) == null) {
                cache.put(o1, CardPart.fromCardModel(o1, model));
            }

            if (cache.get(o2) == null) {
                cache.put(o2, CardPart.fromCardModel(o2, model));
            }

            ExecutionContext.getContext().setCurrentCard(cache.get(o1));
            Value o1Value = expression.evaluate();

            ExecutionContext.getContext().setCurrentCard(cache.get(o2));
            Value o2Value = expression.evaluate();

            ExecutionContext.getContext().setCurrentCard(null);

            return o1Value.compareTo(o2Value, sortStyle);

        } catch (HtException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
