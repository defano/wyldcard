package com.defano.hypertalk.comparator;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.enums.SortDirection;
import com.defano.hypertalk.ast.model.enums.SortStyle;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import com.defano.wyldcard.part.button.ButtonPart;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.card.CardPart;
import com.defano.wyldcard.part.field.FieldPart;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;

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
            PartSpecifier originalMe = context.getStackFrame().getMe();
            CardPart originalCard = context.getCurrentCard();

            // Evaluate expression in the context of card o1
            context.setCurrentCard(acquire(o1));
            Value o1Value = evaluate(o1);

            // Evaluate expression in the context of card o2
            context.setCurrentCard(acquire(o2));
            Value o2Value = evaluate(o2);

            // Stop overriding card context in this thread
            context.setCurrentCard(originalCard);
            context.getStackFrame().setMe(originalMe);

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
            Invoke.onDispatch(() -> {
                cache.put(model, CardPart.fromModel(context, model));
            });
        }

        CardPart card = cache.get(model);

        // Shared background fields in cached cards maintain original text; update the shared text context
        for (FieldPart thisPart : card.getFields()) {
            thisPart.getPartModel().setCurrentCardId(model.getId());
        }

        for (ButtonPart thisPart : card.getButtons()) {
            thisPart.getPartModel().setCurrentCardId(model.getId());
        }

        return card;
    }
}
