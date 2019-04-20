package com.defano.hypertalk.comparator;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.SortDirection;
import com.defano.hypertalk.ast.model.SortStyle;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtUncheckedSemanticException;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
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

            // Evaluate expression in the context of card o1
            context.setCurrentCard(acquire(o1));
            Value o1Value = evaluate(o1);

            // Evaluate expression in the context of card o2
            context.setCurrentCard(acquire(o2));
            Value o2Value = evaluate(o2);

            // Stop overriding card context in this thread
            context.setCurrentCard(null);
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
