package com.defano.wyldcard;

import com.defano.hypertalk.ast.model.Destination;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.CircleStack;

import java.util.Set;

@SuppressWarnings("UnusedReturnValue")
public interface NavigationManager {

    Set<Destination> getRecentCards();

    /**
     * Navigates to the given card index, applying a visual effect to the transition. Has no affect if no card with the
     * requested index does not exist in this stack.
     * <p>
     * Note that card index is zero-based, but card's are numbered starting from one from a user's perspective.
     *
     * @param context      The execution context.
     * @param cardIndex    The zero-based index of the card to navigate to.
     * @return The destination card (now visible in the stack window).
     */
    CardPart goCard(ExecutionContext context, StackPart stackPart, int cardIndex, boolean push);

    /**
     * Navigates to the next card in the stack (index + 1); has no affect if the current card is the last card.
     *
     * @param context      The execution context
     * @param stackPart    The stack in which navigation should occur
     * @return The card now visible in the stack window or null if no next card.
     */
    CardPart goNextCard(ExecutionContext context, StackPart stackPart);

    /**
     * Navigates to the previous card in the stack (index - 1); has no affect if the current card is the first card.
     *
     * @param context      The execution context
     * @param stackPart    The stack in which navigation should occur
     * @return The card now visible in the stack window or null if no previous card.
     */
    CardPart goPrevCard(ExecutionContext context, StackPart stackPart);

    /**
     * Navigates to the previous destination in the {@link CircleStack}.
     *
     * @param context      The execution context
     * @return The card now visible in the stack window, or null if no card available to pop
     */
    CardPart goBack(ExecutionContext context);

    /**
     * Navigates to the next card in the navigation backstack. If the current card is already at the head (top) of the
     * stack, then this method navigates the
     * @param context
     * @return
     */
    CardPart goForth(ExecutionContext context);

    void push(Destination destination);

    Destination pop();

    /**
     * Navigates to the first card in the stack.
     *
     * @param context      The execution context
     * @param stackPart    The stack in which navigation should occur
     * @return The first card in the stack
     */
    CardPart goFirstCard(ExecutionContext context, StackPart stackPart);

    /**
     * Navigates to the last card in the stack.
     *
     * @param context      The execution context
     * @param stackPart    The stack in which navigation should occur
     * @return The last card in the stack
     */
    CardPart goLastCard(ExecutionContext context, StackPart stackPart);

    /**
     * Attempts to navigate to a stack (displaying that stack's current card in the stack window).
     *
     * @param context       The execution context
     * @param stackName     The name or path of the stack to navigate to
     * @param inNewWindow   True to open the stack in a new window, false to replace the current stack with this one
     * @param withoutDialog True to prompt the user to find the stack if WyldCard can't locate it
     * @return The card of the new stack now active in the stack window, or null if the stack could not be located or opened.
     */
    CardPart goStack(ExecutionContext context, String stackName, boolean inNewWindow, boolean withoutDialog);

    /**
     * Attempts to navigate to the given destination, applying a visual effect as requested and blocking the thread
     * until the navigation and animation has completed.
     *
     * @param context      The execution context
     * @param destination  The destination to navigate to
     * @return The destination card
     */
    CardPart goDestination(ExecutionContext context, Destination destination) throws HtSemanticException;

    CircleStack<Destination> getBackstack();
}
