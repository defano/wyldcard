package com.defano.hypertalk.comparator;

import com.defano.wyldcard.parts.card.CardLayerPart;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;

import java.util.Comparator;
import java.util.HashMap;

public class CardExpressionComparator implements Comparator<CardModel> {

    private final Expression expression;
    private final SortStyle sortStyle;
    private final SortDirection direction;

    private HashMap<CardModel, CardPart> cache = new HashMap<>();

    public CardExpressionComparator(Expression expression, SortStyle sortStyle, SortDirection direction) {
        this.expression = expression;
        this.sortStyle = sortStyle;
        this.direction = direction;
    }

    @Override
    public int compare(CardModel o1, CardModel o2) {
        try {
            // Evaluate expression in the context of card o1
            ExecutionContext.getContext().setCurrentCard(acquire(o1));
            ExecutionContext.getContext().pushMe(new PartIdSpecifier(Owner.STACK, PartType.CARD, o1.getId()));
            Value o1Value = expression.evaluate();
            ExecutionContext.getContext().popMe();

            // Evaluate expression in the context of card o2
            ExecutionContext.getContext().setCurrentCard(acquire(o2));
            ExecutionContext.getContext().pushMe(new PartIdSpecifier(Owner.STACK, PartType.CARD, o2.getId()));
            Value o2Value = expression.evaluate();
            ExecutionContext.getContext().popMe();

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
            cache.put(model, CardPart.skeletonFromModel(model));
        }

        CardPart card = cache.get(model);

        // Shared background fields in cached cards maintain original text; update the shared text context
        for (CardLayerPart thisPart : card.getCardParts()) {
            ((CardLayerPartModel) thisPart.getPartModel()).setCurrentCardId(model.getId());
        }

        return card;
    }
}
