package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.layouts.StackWindow;

public class WyldCardNavigationManager implements NavigationManager {

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public CardPart goCard(ExecutionContext context, StackPart stackPart, int cardIndex, boolean push) {
        CardPart cardPart = stackPart.goCard(context, cardIndex);

        // When requested, push the current card onto the backstack
        if (push) {
            Destination destination = new Destination(stackPart.getStackModel(), cardPart.getId(context));
            WyldCard.getInstance().getStackManager().getBackstack().push(destination);
        }

        return cardPart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public CardPart goNextCard(ExecutionContext context, StackPart stackPart) {
        if (stackPart.getStackModel().getCurrentCardIndex() + 1 < stackPart.getStackModel().getCardCount()) {
            return goCard(context, stackPart, stackPart.getStackModel().getCurrentCardIndex() + 1, true);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public CardPart goPrevCard(ExecutionContext context, StackPart stackPart) {
        if (stackPart.getStackModel().getCurrentCardIndex() - 1 >= 0) {
            return goCard(context, stackPart, stackPart.getStackModel().getCurrentCardIndex() - 1, true);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public CardPart goBack(ExecutionContext context, StackPart stackPart) {
        try {
            return goDestination(context, WyldCard.getInstance().getStackManager().getBackstack().peekBack());
        } catch (HtSemanticException e) {
            throw new IllegalStateException("Bug! Backstack contains bogus destination.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public CardPart goFirstCard(ExecutionContext context, StackPart stackPart) {
        return goCard(context, stackPart, 0, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public CardPart goLastCard(ExecutionContext context, StackPart stackPart) {
        return goCard(context, stackPart, stackPart.getStackModel().getCardCount() - 1, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goStack(ExecutionContext context, String stackName, boolean inNewWindow, boolean withoutDialog) {
        try {
            RemoteNavigationOptions navOptions = new RemoteNavigationOptions(inNewWindow, withoutDialog);
            Destination stackDestination = Destination.ofStack(new ExecutionContext(), stackName, navOptions);

            if (stackDestination == null) {
                return null;
            }

            return goDestination(new ExecutionContext(), stackDestination);
        } catch (HtSemanticException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goDestination(ExecutionContext context, Destination destination) throws HtSemanticException {
        return ThreadUtils.callCheckedAndWaitAsNeeded(() -> {
            StackWindow stackWindow = WyldCard.getInstance().getWindowManager().findWindowForStack(destination.getStack());
            context.bind(stackWindow.getStack());
            stackWindow.setVisible(true);
            stackWindow.requestFocus();

            Integer cardIndex = destination.getStack().getIndexOfCardId(destination.getCardId());
            if (cardIndex != null) {
                CardPart card = goCard(context, stackWindow.getStack(), cardIndex, destination.getDirection() == null);
                WyldCard.getInstance().getStackManager().getBackstack().adjust(destination.getDirection());
                return card;
            }

            throw new HtSemanticException("Can't find that card.");
        }, HtSemanticException.class);
    }

}
