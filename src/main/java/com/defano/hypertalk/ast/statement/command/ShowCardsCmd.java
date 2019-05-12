package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.preemption.Preemption;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.NavigationManager;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.part.stack.StackPart;
import com.defano.wyldcard.part.wyldcard.WyldCardPart;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class ShowCardsCmd extends Command {

    private final boolean showAll;
    private final boolean showMarked;
    private final Expression showCount;
    @Inject
    private NavigationManager navigationManager;

    public ShowCardsCmd(ParserRuleContext context, boolean showAll, boolean showMarked, Expression showCount) {
        super(context, "show");
        this.showAll = showAll;
        this.showMarked = showMarked;
        this.showCount = showCount;
    }

    @Override
    protected void onExecute(ExecutionContext context) throws HtException, Preemption {
        StackPart currentStack = context.getCurrentStack();
        int cardCount = currentStack.getCardCountProvider().blockingFirst();

        if (showMarked) {
            showCards(context, currentStack, cardCount, true);
        } else if (showAll) {
            showCards(context, currentStack, cardCount, false);
        } else {
            Value cardsToShow = showCount.evaluate(context);
            if (cardsToShow.isInteger()) {
                showCards(context, currentStack, showCount.evaluate(context).integerValue(), false);
            } else {
                throw new HtSemanticException("Expected the number of cards to show.");
            }
        }
    }

    private void showCards(ExecutionContext context, StackPart currentStack, int count, boolean onlyMarked) {
        int currentCard = currentStack.getDisplayedCard().getPartModel().getCardIndexInStack();
        int cardsInStack = currentStack.getCardCountProvider().blockingFirst();
        int shownCards = 0;

        Value lockMessages = WyldCard.getInstance().getWyldCardPart().get(context, WyldCardPart.PROP_LOCKMESSAGES);
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardPart.PROP_LOCKMESSAGES, new Value(true));

        // Keep cycling until we've shown requested count (count may exceed total number of cards in stack)
        do {

            // Show cards from this point to the end of the stack...
            for (int idx = currentCard; idx < cardsInStack && shownCards <= count; idx++) {
                if (!onlyMarked || currentStack.getPartModel().getCardModel(idx).isMarked(context)) {
                    navigationManager.goCard(context, currentStack, idx, false);
                    shownCards++;
                }
            }

            // ... then show cards starting at 0 to this point
            for (int idx = 0; idx <= currentCard && shownCards <= count; idx++) {
                if (!onlyMarked || currentStack.getPartModel().getCardModel(idx).isMarked(context)) {
                    navigationManager.goCard(context, currentStack, idx, false);
                    shownCards++;
                }
            }

        } while (shownCards <= count);

        // Restore lockMessages setting
        WyldCard.getInstance().getWyldCardPart().set(context, WyldCardPart.PROP_LOCKMESSAGES, lockMessages);
    }

}
