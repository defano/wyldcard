package com.defano.wyldcard.parts.stack;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.fx.CurtainManager;
import com.defano.wyldcard.parts.PartException;
import com.defano.wyldcard.parts.bkgnd.BackgroundModel;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.window.StackWindow;
import com.defano.wyldcard.window.WindowManager;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.SystemMessage;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.model.specifiers.VisualEffectSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the controller object of the stack itself. See {@link StackModel} for the data model.
 * <p>
 * This view is "virtual" because a stack has no view aside from the card that is currently displayed in it. Thus, this
 * class has no associated Swing component and cannot be added to a view hierarchy.
 */
public class StackPart implements PropertyChangeObserver {

    private StackModel stackModel;
    private CardPart currentCard;

    private final List<StackObserver> stackObservers = new ArrayList<>();
    private final List<StackNavigationObserver> stackNavigationObservers = new ArrayList<>();
    private final Subject<Integer> cardCountProvider = BehaviorSubject.createDefault(0);
    private final Subject<Optional<CardPart>> cardClipboardProvider = BehaviorSubject.createDefault(Optional.empty());

    private StackPart() {}

    public static StackPart newStack(ExecutionContext context) {
        return fromStackModel(context, StackModel.newStackModel("Untitled"));
    }

    public static StackPart fromStackModel(ExecutionContext context, StackModel model) {
        StackPart stackPart = new StackPart();
        stackPart.stackModel = model;
        stackPart.cardCountProvider.onNext(model.getCardCount());
        stackPart.stackModel.addPropertyChangedObserver(stackPart);
        stackPart.currentCard = stackPart.openCard(context, model.getCurrentCardIndex());

        return stackPart;
    }

    public void bindToWindow(ExecutionContext context, StackWindow stackWindow) {
        stackWindow.bindModel(this);

        goCard(context, stackModel.getCurrentCardIndex(), null, false);
        fireOnStackOpened();
        fireOnCardDimensionChanged(stackModel.getDimension(context));

        getStackModel().receiveMessage(new ExecutionContext(), SystemMessage.OPEN_STACK.messageName);
        fireOnCardOpened(getDisplayedCard());

        ToolsContext.getInstance().reactivateTool(currentCard.getCanvas());
    }

    /**
     * Gets the data model associated with this stack.
     * @return The stack model.
     */
    public StackModel getStackModel() {
        return stackModel;
    }

    /**
     * Navigates to the given card index, applying a visual effect to the transition. Has no affect if no card with the
     * requested index exists in this stack.
     *
     * Note that card index is zero-based, but card's are numbered starting from one from a user's perspective.
     *
     *
     * @param context
     * @param cardIndex The zero-based index of the card to navigate to.
     * @param visualEffect The visual effect to apply to the transition
     * @return The destination card (now visible in the stack window).
     */
    @RunOnDispatch
    public CardPart goCard(ExecutionContext context, int cardIndex, VisualEffectSpecifier visualEffect, boolean pushToBackstack) {
        CardPart destination;

        if (visualEffect == null) {
            destination = go(context, cardIndex, pushToBackstack);
        } else {
            CurtainManager.getInstance().setScreenLocked(true);
            destination = go(context, cardIndex, pushToBackstack);
            CurtainManager.getInstance().unlockScreenWithEffect(visualEffect);
        }

        this.currentCard = destination;
        return destination;
    }

    /**
     * Navigates to the next card in the stack; has no affect if the current card is the last card.
     * @return The card now visible in the stack window or null if no next card.
     */
    @RunOnDispatch
    public CardPart goNextCard(ExecutionContext context, VisualEffectSpecifier visualEffect) {
        if (stackModel.getCurrentCardIndex() + 1 < stackModel.getCardCount()) {
            return goCard(context, stackModel.getCurrentCardIndex() + 1, visualEffect, true);
        } else {
            return null;
        }
    }

    /**
     * Navigates to the previous card in the stack; has no affect if the current card is the first card.
     * @return The card now visible in the stack window or null if no previous card.
     */
    @RunOnDispatch
    public CardPart goPrevCard(ExecutionContext context, VisualEffectSpecifier visualEffect) {
        if (stackModel.getCurrentCardIndex() - 1 >= 0) {
            return goCard(context, stackModel.getCurrentCardIndex() - 1, visualEffect, true);
        } else {
            return null;
        }
    }

    /**
     * Navigates to the last card on the backstack; has no affect if the backstack is empty.
     * @return The card now visible in the stack window, or null if no card available to pop
     */
    @RunOnDispatch
    public CardPart popCard(ExecutionContext context, VisualEffectSpecifier visualEffect) {
        if (!stackModel.getBackStack().isEmpty()) {
            try {
                CardModel model = (CardModel) getStackModel().findPart(context, new PartIdSpecifier(Owner.STACK, PartType.CARD, stackModel.getBackStack().pop()));
                return goCard(context, getStackModel().getIndexOfCard(model), visualEffect, false);
            } catch (PartException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Navigates to the current card; useful only to apply a visual effect to the current card
     * image.
     * @param visualEffectSpecifier The visual effect to apply
     * @param context
     * @return The current card
     */
    @RunOnDispatch
    public CardPart goThisCard(VisualEffectSpecifier visualEffectSpecifier, ExecutionContext context) {
        return goCard(context, stackModel.getCurrentCardIndex(), visualEffectSpecifier, false);
    }

    /**
     * Navigates to the first card in the stack.
     * @return The first card in the stack
     */
    @RunOnDispatch
    public CardPart goFirstCard(ExecutionContext context, VisualEffectSpecifier visualEffect) {
        return goCard(context, 0, visualEffect, true);
    }

    /**
     * Navigates to the last card in the stack.
     * @return The last card in the stack
     */
    @RunOnDispatch
    public CardPart goLastCard(ExecutionContext context, VisualEffectSpecifier visualEffect) {
        return goCard(context, stackModel.getCardCount() - 1, visualEffect, true);
    }

    /**
     * Deletes the current card provided there are more than one card in the stack.
     * @return The card now visible in the stack window, or null if the current card could not be deleted.
     * @param context
     */
    @RunOnDispatch
    public CardPart deleteCard(ExecutionContext context) {
        if (canDeleteCard(context)) {
            ToolsContext.getInstance().setIsEditingBackground(false);

            int deletedCardIndex = stackModel.getCurrentCardIndex();
            stackModel.deleteCardModel();
            cardCountProvider.onNext(stackModel.getCardCount());
            fireOnCardOrderChanged();

            return activateCard(context, deletedCardIndex == 0 ? 0 : deletedCardIndex - 1);
        }

        WyldCard.getInstance().showErrorDialog(new HtSemanticException("This card cannot be deleted because it or its background is marked as \"Can't Delete\"."));
        return null;
    }

    /**
     * Creates a new card with a new background. Differs from {@link #newCard(ExecutionContext)} in that {@link #newCard(ExecutionContext)} creates a
     * new card with the same background as the current card.
     *
     * @return The newly created card.
     * @param context
     */
    @RunOnDispatch
    public CardPart newBackground(ExecutionContext context) {
        ToolsContext.getInstance().setIsEditingBackground(false);

        stackModel.newCardWithNewBackground();
        cardCountProvider.onNext(stackModel.getCardCount());
        fireOnCardOrderChanged();

        return goNextCard(context, null);
    }

    /**
     * Creates a new card with the same background as the current card. See {@link #newBackground(ExecutionContext)} to create a new
     * card with a new background.
     *
     * @return The newly created card.
     * @param context
     */
    @RunOnDispatch
    public CardPart newCard(ExecutionContext context) {
        ToolsContext.getInstance().setIsEditingBackground(false);

        stackModel.newCard(currentCard.getCardModel().getBackgroundId());
        cardCountProvider.onNext(stackModel.getCardCount());
        fireOnCardOrderChanged();

        return goNextCard(context, null);
    }

    /**
     * Removes the current card from the stack and places it into the card clipboard (for pasting elsewhere in the
     * stack).
     * @param context
     */
    @RunOnDispatch
    public void cutCard(ExecutionContext context) {
        cardClipboardProvider.onNext(Optional.of(getDisplayedCard()));
        cardCountProvider.onNext(stackModel.getCardCount());

        deleteCard(context);
    }

    /**
     * Copies the displayed card to the card clipboard for pasting elsewhere in the stack.
     */
    @RunOnDispatch
    public void copyCard() {
        cardClipboardProvider.onNext(Optional.of(getDisplayedCard()));
    }

    /**
     * Adds the card presently held in the card clipboard to the stack in the current card's position. Has no affect
     * if the clipboard is empty.
     * @param context
     */
    @RunOnDispatch
    public void pasteCard(ExecutionContext context) {
        if (cardClipboardProvider.blockingFirst().isPresent()) {
            ToolsContext.getInstance().setIsEditingBackground(false);

            CardModel card = cardClipboardProvider.blockingFirst().get().getCardModel().copyOf();
            card.relinkParentPartModel(getStackModel());
            card.defineProperty(CardModel.PROP_ID, new Value(getStackModel().getNextCardId()), true);

            stackModel.insertCard(card);
            cardCountProvider.onNext(stackModel.getCardCount());
            fireOnCardOrderChanged();

            goNextCard(context, null);
        }
    }

    /**
     * Gets an observable object containing the contents of the card clipboard.
     * @return The card clipboard provider.
     */
    public Observable<Optional<CardPart>> getCardClipboardProvider() {
        return cardClipboardProvider;
    }

    /**
     * Gets the currently displayed card.
     * @return The current card
     */
    public CardPart getDisplayedCard() {
        return currentCard;
    }

    /**
     * Invalidates the card cache; useful only if modifying this stack's underlying stack model (i.e., as a
     * result of card sorting or re-ordering).
     *
     * @param context
     * @param cardIndex - The index of the card in the stack to transition to after invalidating the cache.
     */
    @RunOnDispatch
    public void invalidateCache(ExecutionContext context, int cardIndex) {
        this.currentCard.partClosed(context);
        this.currentCard = openCard(context, getStackModel().getCurrentCardIndex());

        this.cardCountProvider.onNext(stackModel.getCardCount());

        fireOnCardOrderChanged();
        goCard(context, cardIndex, null, false);
    }

    /**
     * Gets an observable object containing the number of card in the stack.
     * @return The card count provider
     */
    public Observable<Integer> getCardCountProvider() {
        return cardCountProvider;
    }

    /**
     * Adds an observer of stack changes.
     * @param observer The observer
     */
    public void addObserver (StackObserver observer) {
        stackObservers.add(observer);
    }

    /**
     * Removes an observer of stack changes.
     * @param observer The observer
     */
    public void removeObserver (StackObserver observer) {
        stackObservers.remove(observer);
    }

    /**
     * Adds an observer of stack navigation changes (i.e., user changed cards)
     * @param observer The observer
     */
    public void addNavigationObserver(StackNavigationObserver observer) {
        stackNavigationObservers.add(observer);
    }

    /**
     * Removes an observer of stack navigation changes.
     * @param observer The observer to remove
     */
    public void removeNavigationObserver(StackNavigationObserver observer) {
        stackNavigationObservers.remove(observer);
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case StackModel.PROP_NAME:
                fireOnStackNameChanged(newValue.stringValue());
                break;

            case StackModel.PROP_HEIGHT:
            case StackModel.PROP_WIDTH:
                // Resize the window
                fireOnCardDimensionChanged(getStackModel().getDimension(context));

                // Re-load the card model into the size
                activateCard(context, stackModel.getCurrentCardIndex());
                break;

            case StackModel.PROP_RESIZABLE:
                WindowManager.getInstance().getStackWindow().setAllowResizing(newValue.booleanValue());
                break;
        }
    }

    @RunOnDispatch
    private CardPart openCard(ExecutionContext context, int index) {
        try {
            currentCard = CardPart.fromPositionInStack(context, index, stackModel);
            ThreadUtils.invokeAndWaitAsNeeded(() -> currentCard.partOpened(context));
            return currentCard;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    @RunOnDispatch
    private CardPart go(ExecutionContext context, int cardIndex, boolean push) {
        // Nothing to do if navigating to current card or an invalid card index
        if (cardIndex == stackModel.getCurrentCardIndex() || cardIndex < 0 || cardIndex >= stackModel.getCardCount()) {
            return getDisplayedCard();
        }

        deactivateCard(context, push);
        return activateCard(context, cardIndex);
    }

    @RunOnDispatch
    private void deactivateCard(ExecutionContext context, boolean push) {
        CardPart displayedCard = getDisplayedCard();

        // Deactivate paint tool before doing anything (to commit in-fight changes)
        ToolsContext.getInstance().getPaintTool().deactivate();

        // Stop editing background when card changes
        ToolsContext.getInstance().setIsEditingBackground(false);

        // When requested, push the current card onto the backstack
        if (push) {
            stackModel.getBackStack().push(displayedCard.getId(context));
        }

        // Notify observers that current card is going away
        fireOnCardClosing(displayedCard);
        displayedCard.partClosed(context);
    }

    @RunOnDispatch
    private CardPart activateCard(ExecutionContext context, int cardIndex) {

        try {
            // Change card
            stackModel.setCurrentCardIndex(cardIndex);
            currentCard = openCard(context, cardIndex);

            // Notify observers of new card
            fireOnCardOpened(currentCard);

            // Reactivate paint tool on new card's canvas
            ToolsContext.getInstance().reactivateTool(currentCard.getCanvas());

            return currentCard;

        } catch (Exception e) {
            throw new RuntimeException("Failed to activate card.", e);
        }
    }

    private boolean canDeleteCard(ExecutionContext context) {
        long cardCountInBackground = stackModel.getCardsInBackground(getDisplayedCard().getCardModel().getBackgroundId()).size();

        return stackModel.getCardCount() > 1 &&
                !getDisplayedCard().getCardModel().getKnownProperty(context, CardModel.PROP_CANTDELETE).booleanValue() &&
                (cardCountInBackground > 1 || !getDisplayedCard().getCardModel().getBackgroundModel().getKnownProperty(context, BackgroundModel.PROP_CANTDELETE).booleanValue());
    }

    private void fireOnStackOpened () {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onStackOpened(StackPart.this);
            }
        });
    }

    private void fireOnCardClosing (CardPart closingCard) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackNavigationObserver observer : stackNavigationObservers) {
                observer.onCardClosed(closingCard);
            }
        });
    }

    private void fireOnCardOpened (CardPart openedCard) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackNavigationObserver observer : stackNavigationObservers) {
                observer.onCardOpened(openedCard);
            }
        });
    }

    private void fireOnCardDimensionChanged(Dimension newDimension) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onStackDimensionChanged(newDimension);
            }
        });
    }

    private void fireOnStackNameChanged(String newName) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onStackNameChanged(newName);
            }
        });
    }

    private void fireOnCardOrderChanged() {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackObserver observer : stackObservers) {
                observer.onCardOrderChanged();
            }
        });
    }
}
