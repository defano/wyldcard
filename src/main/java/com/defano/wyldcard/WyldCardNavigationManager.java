package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.ast.model.RemoteNavigationOptions;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.util.CircleStack;
import com.defano.wyldcard.window.layouts.StackWindow;

import java.util.EmptyStackException;
import java.util.Set;
import java.util.Stack;

public class WyldCardNavigationManager implements NavigationManager {

    // Circular stack of recently visited destinations
    private final static CircleStack<Destination> backstack = new CircleStack<>(20);
    private final static Stack<Destination> pushPopStack = new Stack<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public CircleStack<Destination> getNavigationStack() {
        return backstack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Destination> getRecentCards() {
        return getNavigationStack().asSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goNextCard(ExecutionContext context, StackPart stackPart) {
        int currentCard = stackPart.getStackModel().getCurrentCardIndex();

        if (currentCard + 1 < stackPart.getStackModel().getCardCount()) {
            return goCard(context, stackPart, currentCard + 1, true);
        } else {
            return goCard(context, stackPart, 0, true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goPrevCard(ExecutionContext context, StackPart stackPart) {
        int currentCard = stackPart.getStackModel().getCurrentCardIndex();

        if (currentCard - 1 >= 0) {
            return goCard(context, stackPart, currentCard - 1, true);
        } else {
            return goCard(context, stackPart, stackPart.getStackModel().getCardCount() - 1, true);
        }
    }

    @Override
    public void push(Destination destination) {
        pushPopStack.push(destination);
    }

    @Override
    public Destination pop() {
        try {
            return pushPopStack.pop();
        } catch (EmptyStackException e) {
            return Destination.ofStack(new ExecutionContext(), "Home", new RemoteNavigationOptions(false, false));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goBack(ExecutionContext context) {
        try {
            return goDestination(context, getNavigationStack().back(), false);
        }

        // Indicates card on stack was deleted; go back again
        catch (HtSemanticException e) {
            return goBack(context);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goForth(ExecutionContext context) {
        try {
            return goDestination(context, getNavigationStack().forward(), false);
        }

        // Indicates card on stack was deleted; go forth again
        catch (HtSemanticException e) {
            return goForth(context);
        }
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
        return Invoke.onDispatch(() -> {
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
        return goDestination(context, destination, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardPart goCard(ExecutionContext context, StackPart stackPart, int cardIndex, boolean push) {
        return Invoke.onDispatch(() -> {
            CardPart cardPart;

            // Nothing to do if navigating to current card or an invalid card index
            if (cardIndex == stackPart.getStackModel().getCurrentCardIndex() ||
                    cardIndex < 0 ||
                    cardIndex >= stackPart.getStackModel().getCardCount()) {

                cardPart = stackPart.getDisplayedCard();
            } else {
                stackPart.closeCard(context, stackPart.getPartModel().getCardModel(cardIndex));
                cardPart = stackPart.openCard(context, cardIndex, stackPart.getPartModel().getCurrentCard());
            }

            // When requested, push the current card onto the backstack
            if (push) {
                Destination destination = new Destination(stackPart.getStackModel(), cardPart.getId(context));
                getNavigationStack().push(destination);
            }

            // Update the current card in the context
            context.setCurrentCard(cardPart);
            return cardPart;
        });
    }

    public CardPart goDestination(ExecutionContext context, Destination destination, boolean push) throws HtSemanticException {
        return Invoke.onDispatch(() -> {
            StackWindow stackWindow = WyldCard.getInstance().getWindowManager().findWindowForStack(destination.getStack());
            context.bindStack(stackWindow.getDisplayedStack());
            stackWindow.setVisible(true);
            stackWindow.requestFocus();

            Integer cardIndex = destination.getStack().getIndexOfCardId(destination.getCardIndex());
            if (cardIndex != null) {
                return goCard(context, stackWindow.getDisplayedStack(), cardIndex, push);
            }

            throw new HtSemanticException("Can't find that card.");
        }, HtSemanticException.class);
    }
}
