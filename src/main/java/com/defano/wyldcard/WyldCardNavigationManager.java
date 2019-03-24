package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.NavigationStack;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.layouts.StackWindow;

import java.util.Set;

public class WyldCardNavigationManager implements NavigationManager {

    private final static NavigationStack navigationStack = new NavigationStack(20);
    private final static NavigationStack pushPopStack = new NavigationStack(20);

    @Override
    public NavigationStack getNavigationStack() {
        return navigationStack;
    }

    @Override
    public NavigationStack getPushPopStack() {
        return pushPopStack;
    }

    @Override
    public Set<Destination> getRecentCards() {
        return getNavigationStack().asSet();
    }

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
            getNavigationStack().push(destination);
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

    @Override
    public void push(Destination destination) {
        pushPopStack.push(destination);
    }

    @Override
    public Destination pop() {
        return pushPopStack.pop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public CardPart goBack(ExecutionContext context) {
        try {
            return goDestination(context, getNavigationStack().back(), false);
        } catch (HtSemanticException e) {
            throw new IllegalStateException("Bug! Backstack contains bogus destination.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public CardPart goForth(ExecutionContext context) {
        try {
            return goDestination(context, getNavigationStack().forward(), false);
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
        return goDestination(context, destination, true);
    }

    private CardPart goDestination(ExecutionContext context, Destination destination, boolean push) throws HtSemanticException {
        return ThreadUtils.callCheckedAndWaitAsNeeded(() -> {
            StackWindow stackWindow = WyldCard.getInstance().getWindowManager().findWindowForStack(destination.getStack());
            context.bind(stackWindow.getStack());
            stackWindow.setVisible(true);
            stackWindow.requestFocus();

            Integer cardIndex = destination.getStack().getIndexOfCardId(destination.getCardIndex());
            if (cardIndex != null) {
                return goCard(context, stackWindow.getStack(), cardIndex, push);
            }

            throw new HtSemanticException("Can't find that card.");
        }, HtSemanticException.class);
    }
}
