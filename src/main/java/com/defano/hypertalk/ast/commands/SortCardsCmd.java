package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.SortDirection;
import com.defano.hypertalk.ast.common.SystemMessage;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.comparator.CardExpressionComparator;
import com.defano.hypertalk.comparator.SortStyle;
import com.defano.hypertalk.exception.HtException;

import java.util.List;

public class SortCardsCmd extends Command {

    private final boolean markedCards;
    private final SortDirection direction;
    private final SortStyle style;
    private final Expression expression;
    private final PartExp background;

    public SortCardsCmd(boolean markedCards, SortDirection direction, SortStyle style, Expression expression) {
        this(markedCards, null, direction, style, expression);
    }

    public SortCardsCmd(boolean markedCards, PartExp background, SortDirection direction, SortStyle style, Expression expression) {
        super("sort");

        this.markedCards = markedCards;
        this.direction = direction;
        this.style = style;
        this.expression = expression;
        this.background = background;
    }

    @Override
    public void onExecute() throws HtException {
        int thisCardId = HyperCard.getInstance().getDisplayedCard().getCardModel().getId();
        List<CardModel> cards = HyperCard.getInstance().getStack().getStackModel().getCardModels();

        for (CardModel thisModel : cards) {
            System.err.println(thisModel.getId());
        }

        cards.sort(new CardExpressionComparator(HyperCard.getInstance().getStack().getStackModel(), expression, style));

        for (CardModel thisModel : cards) {
            System.err.println(thisModel.getId());
        }

        HyperCard.getInstance().getStack().getStackModel().setCardModels(cards);

        HyperCard.getInstance().getStack().goCard(indexOfCardId(thisCardId), null);
    }

    private int indexOfCardId(int id) {
        List<CardModel> cardModels = HyperCard.getInstance().getStack().getStackModel().getCardModels();
        for (int index = 0; index < cardModels.size(); index++) {
            if (cardModels.get(index).getId() == id) {
                return index;
            }
        }

        throw new IllegalStateException("Bug! No card with that ID in stack.");
    }

}
