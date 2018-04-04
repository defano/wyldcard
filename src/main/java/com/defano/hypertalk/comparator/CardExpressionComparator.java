package com.defano.hypertalk.comparator;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.*;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import com.defano.wyldcard.parts.card.CardLayerPart;
import com.defano.wyldcard.parts.card.CardLayerPartModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;

import java.util.Comparator;
import java.util.HashMap;

public class CardExpressionComparator implements Comparator<CardModel> {

    private final ExecutionContext context;
    private final Expression expression;
    private final SortStyle sortStyle;
    private final SortDirection direction;

    private HashMap<CardModel, CardPart> cache = new HashMap<>();
    private HashMap<CardModel, Value> sanityCache = new HashMap<>();

    public CardExpressionComparator(ExecutionContext context, Expression expression, SortStyle sortStyle, SortDirection direction) {
        this.context = context;
        this.expression = expression;
        this.sortStyle = sortStyle;
        this.direction = direction;
    }

    @Override
    public int compare(CardModel o1, CardModel o2) {
        try {
            // Evaluate expression in the context of card o1
            context.setCurrentCard(acquire(o1));
            context.pushMe(new PartIdSpecifier(Owner.STACK, PartType.CARD, o1.getId(context)));
            Value o1Value = evaluate(o1);
            context.popMe();

            // Evaluate expression in the context of card o2
            context.setCurrentCard(acquire(o2));
            context.pushMe(new PartIdSpecifier(Owner.STACK, PartType.CARD, o2.getId(context)));
            Value o2Value = evaluate(o2);
            context.popMe();

            // Stop overriding card context in this thread
            context.setCurrentCard(null);

            if (direction == SortDirection.ASCENDING) {
                return o1Value.compareTo(o2Value, sortStyle);
            } else {
                return o2Value.compareTo(o1Value, sortStyle);
            }

        } catch (HtException e) {
            throw new HtUncheckedSemanticException(e);
        }
    }

    private Value evaluate(CardModel model) throws HtException {
        if (sanityCache.containsKey(model)) {
            return sanityCache.get(model);
        } else {
            Value evaluated = expression.evaluate(context);
            sanityCache.put(model, evaluated);
            return evaluated;
        }
    }

    private CardPart acquire(CardModel model) {

        if (!cache.containsKey(model)) {
            ThreadUtils.invokeAndWaitAsNeeded(() -> {
                try {
                    cache.put(model, CardPart.skeletonFromModel(context, model));
                } catch (HtException e) {
                    // Bug!
                }
            });
        }

        CardPart card = cache.get(model);

        // Shared background fields in cached cards maintain original text; update the shared text context
        for (CardLayerPart thisPart : card.getCardParts()) {
            ((CardLayerPartModel) thisPart.getPartModel()).setCurrentCardId(model.getId(context));
        }

        return card;
    }
}
