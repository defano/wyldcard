package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.CircleStack;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.layouts.StackWindow;

import java.util.Set;
import java.util.Stack;

public class WyldCardNavigationManager implements NavigationManager {

    // Circular stack of recently visited destinations
    private final static CircleStack<Destination> backstack = new CircleStack<>(20);
    private final static Stack<Destination> pushPopStack = new Stack<>();

    @Override
    public CircleStack<Destination> getBackstack() {
        return backstack;
    }

    @Override
    public Set<Destination> getRecentCards() {
        return getBackstack().asSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goCard(ExecutionContext context, StackPart stackPart, int cardIndex, boolean push) {
        return ThreadUtils.callAndWaitAsNeeded(() -> {
            CardPart cardPart = stackPart.goCard(context, cardIndex);

            // When requested, push the current card onto the backstack
            if (push) {
                Destination destination = new Destination(stackPart.getStackModel(), cardPart.getId(context));
                getBackstack().push(destination);
            }

            return cardPart;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goNextCard(ExecutionContext context, StackPart stackPart) {
        return ThreadUtils.callAndWaitAsNeeded(() -> {
            if (stackPart.getStackModel().getCurrentCardIndex() + 1 < stackPart.getStackModel().getCardCount()) {
                return goCard(context, stackPart, stackPart.getStackModel().getCurrentCardIndex() + 1, true);
            } else {
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goPrevCard(ExecutionContext context, StackPart stackPart) {
        return ThreadUtils.callAndWaitAsNeeded(() -> {
            if (stackPart.getStackModel().getCurrentCardIndex() - 1 >= 0) {
                return goCard(context, stackPart, stackPart.getStackModel().getCurrentCardIndex() - 1, true);
            } else {
                return null;
            }
        });
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
    public CardPart goBack(ExecutionContext context) {
        return ThreadUtils.callAndWaitAsNeeded(() -> {
            try {
                return goDestination(context, getBackstack().back(), false);
            } catch (HtSemanticException e) {
                throw new IllegalStateException("Bug! Backstack contains bogus destination.", e);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goForth(ExecutionContext context) {
        return ThreadUtils.callAndWaitAsNeeded(() -> {
            try {
                return goDestination(context, getBackstack().forward(), false);
            } catch (HtSemanticException e) {
                throw new IllegalStateException("Bug! Backstack contains bogus destination.", e);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goFirstCard(ExecutionContext context, StackPart stackPart) {
        return goCard(context, stackPart, 0, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goLastCard(ExecutionContext context, StackPart stackPart) {
        return goCard(context, stackPart, stackPart.getStackModel().getCardCount() - 1, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goStack(ExecutionContext context, String stackName, boolean inNewWindow, boolean withoutDialog) {
        return ThreadUtils.callAndWaitAsNeeded(() -> {
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
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goDestination(ExecutionContext context, Destination destination) throws HtSemanticException {
        return ThreadUtils.callAndWaitAsNeeded(() -> goDestination(context, destination, true));
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
