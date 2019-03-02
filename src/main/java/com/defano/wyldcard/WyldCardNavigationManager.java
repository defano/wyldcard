package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.jsegue.SegueName;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.layouts.StackWindow;

public class WyldCardNavigationManager implements NavigationManager {

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public CardPart goCard(ExecutionContext context, StackPart stackPart, int cardIndex, VisualEffectSpecifier visualEffect, boolean pushToBackstack) {
        CardPart currentCard;

        if (visualEffect == null || visualEffect.name == SegueName.PLAIN) {
            currentCard = goCard(context, stackPart, cardIndex, pushToBackstack);
        } else {
            stackPart.getCurtainManager().setScreenLocked(context, true);
            currentCard = goCard(context, stackPart, cardIndex, pushToBackstack);
            stackPart.getCurtainManager().unlockScreenWithEffect(context, visualEffect);
        }

        return currentCard;
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public CardPart goNextCard(ExecutionContext context, StackPart stackPart, VisualEffectSpecifier visualEffect) {
        if (stackPart.getStackModel().getCurrentCardIndex() + 1 < stackPart.getStackModel().getCardCount()) {
            return goCard(context, stackPart, stackPart.getStackModel().getCurrentCardIndex() + 1, visualEffect, true);
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public CardPart goPrevCard(ExecutionContext context, StackPart stackPart, VisualEffectSpecifier visualEffect) {
        if (stackPart.getStackModel().getCurrentCardIndex() - 1 >= 0) {
            return goCard(context, stackPart, stackPart.getStackModel().getCurrentCardIndex() - 1, visualEffect, true);
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public CardPart goPopCard(ExecutionContext context, StackPart stackPart, VisualEffectSpecifier visualEffect) {
        if (!WyldCard.getInstance().getStackManager().getBackstack().isEmpty()) {
            try {
                Destination poppedDestination = WyldCard.getInstance().getStackManager().getBackstack().pop();
                CardModel model = (CardModel) poppedDestination.getStack().findPart(context, new PartIdSpecifier(Owner.STACK, PartType.CARD, poppedDestination.getCardId()));
                return goCard(context, stackPart, stackPart.getStackModel().getIndexOfCard(model), visualEffect, false);
            } catch (PartException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public CardPart goFirstCard(ExecutionContext context, StackPart stackPart, VisualEffectSpecifier visualEffect) {
        return goCard(context, stackPart, 0, visualEffect, true);
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public CardPart goLastCard(ExecutionContext context, StackPart stackPart, VisualEffectSpecifier visualEffect) {
        return goCard(context, stackPart, stackPart.getStackModel().getCardCount() - 1, visualEffect, true);
    }

    /** {@inheritDoc} */
    @Override
    public CardPart goStack(ExecutionContext context, String stackName, boolean inNewWindow, boolean withoutDialog) {
        try {
            RemoteNavigationOptions navOptions = new RemoteNavigationOptions(inNewWindow, withoutDialog);
            Destination stackDestination = Destination.ofStack(new ExecutionContext(), stackName, navOptions);

            if (stackDestination == null) {
                return null;
            }

            return goDestination(new ExecutionContext(), stackDestination, null);
        } catch (HtSemanticException e) {
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public CardPart goDestination(ExecutionContext context, Destination destination, VisualEffectSpecifier visualEffect) throws HtSemanticException {
        // This code needs to run on the Swing dispatch thread
        return ThreadUtils.callCheckedAndWaitAsNeeded(() -> {
            StackWindow stackWindow = WyldCard.getInstance().getWindowManager().findWindowForStack(destination.getStack());
            context.bind(stackWindow.getStack());
            stackWindow.setVisible(true);
            stackWindow.requestFocus();

            Integer cardIndex = destination.getStack().getIndexOfCardId(destination.getCardId());
            if (cardIndex != null) {
                return ThreadUtils.callAndWaitAsNeeded(() -> goCard(context, stackWindow.getStack(), cardIndex, visualEffect, true));
            }

            throw new HtSemanticException("Can't find that card.");
        }, HtSemanticException.class);
    }

    /**
     * Attempts to navigate to the specified card; has no effect if the requested card is already the current card or if
     * the requested card refers to an invalid card index.
     * <p>
     * When the requested card is not the current card, has the effect of deactivating the current card and activating
     * the requested card.
     *
     * @param context   The execution context
     * @param cardIndex The index (0-based) card to navigate to
     * @param push      True to add this card to the backstack
     * @return The current card after navigation.
     */
    @RunOnDispatch
    private CardPart goCard(ExecutionContext context, StackPart stackPart, int cardIndex, boolean push) {

        // Nothing to do if navigating to current card or an invalid card index
        if (cardIndex == stackPart.getStackModel().getCurrentCardIndex() ||
                cardIndex < 0 ||
                cardIndex >= stackPart.getStackModel().getCardCount())
        {
            return stackPart.getDisplayedCard();
        }

        stackPart.deactivateCard(context, push);
        return stackPart.activateCard(context, cardIndex);
    }

}
