package com.defano.hypercard.parts.stack;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypercard.parts.PartException;
import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.model.PropertiesModel;
import com.defano.hypercard.parts.model.PropertyChangeObserver;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypercard.window.StackWindow;
import com.defano.hypercard.window.WindowManager;
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

    public static StackPart newStack() {
        return fromStackModel(StackModel.newStackModel("Untitled"));
    }

    public static StackPart fromStackModel(StackModel model) {
        StackPart stackPart = new StackPart();
        stackPart.stackModel = model;
        stackPart.currentCard = stackPart.buildCardPart(model.getCurrentCardIndex());
        stackPart.cardCountProvider.onNext(model.getCardCount());
        stackPart.stackModel.addPropertyChangedObserver(stackPart);

        return stackPart;
    }

    public void bindToWindow(StackWindow stackWindow) {
        stackWindow.bindModel(this);

        goCard(stackModel.getCurrentCardIndex(), null, false);
        fireOnStackOpened();
        fireOnCardDimensionChanged(stackModel.getDimension());

        getStackModel().receiveMessage(SystemMessage.OPEN_STACK.messageName);
        getDisplayedCard().partOpened();
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
     * @param cardIndex The zero-based index of the card to navigate to.
     * @param visualEffect The visual effect to apply to the transition
     * @return The destination card (now visible in the stack window).
     */
    public CardPart goCard(int cardIndex, VisualEffectSpecifier visualEffect, boolean pushToBackstack) {
        CardPart destination;

        if (visualEffect == null) {
            destination = go(cardIndex, pushToBackstack);
        } else {
            CurtainManager.getInstance().setScreenLocked(true);
            destination = go(cardIndex, pushToBackstack);
            CurtainManager.getInstance().unlockScreenWithEffect(visualEffect);
        }

        this.currentCard = destination;
        return destination;
    }

    /**
     * Navigates to the next card in the stack; has no affect if the current card is the last card.
     * @return The card now visible in the stack window or null if no next card.
     */
    public CardPart goNextCard(VisualEffectSpecifier visualEffect) {
        if (stackModel.getCurrentCardIndex() + 1 < stackModel.getCardCount()) {
            return goCard(stackModel.getCurrentCardIndex() + 1, visualEffect, true);
        } else {
            return null;
        }
    }

    /**
     * Navigates to the previous card in the stack; has no affect if the current card is the first card.
     * @return The card now visible in the stack window or null if no previous card.
     */
    public CardPart goPrevCard(VisualEffectSpecifier visualEffect) {
        if (stackModel.getCurrentCardIndex() - 1 >= 0) {
            return goCard(stackModel.getCurrentCardIndex() - 1, visualEffect, true);
        } else {
            return null;
        }
    }

    /**
     * Navigates to the last card on the backstack; has no affect if the backstack is empty.
     * @return The card now visible in the stack window, or null if no card available to pop
     */
    public CardPart popCard(VisualEffectSpecifier visualEffect) {
        if (!stackModel.getBackStack().isEmpty()) {
            try {
                CardModel model = (CardModel) getStackModel().findPart(new PartIdSpecifier(Owner.STACK, PartType.CARD, stackModel.getBackStack().pop()));
                return goCard(getStackModel().getIndexOfCard(model), visualEffect, false);
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
     * @return The current card
     */
    public CardPart goThisCard(VisualEffectSpecifier visualEffectSpecifier) {
        return goCard(stackModel.getCurrentCardIndex(), visualEffectSpecifier, false);
    }

    /**
     * Navigates to the first card in the stack.
     * @return The first card in the stack
     */
    public CardPart goFirstCard(VisualEffectSpecifier visualEffect) {
        return goCard(0, visualEffect, true);
    }

    /**
     * Navigates to the last card in the stack.
     * @return The last card in the stack
     */
    public CardPart goLastCard(VisualEffectSpecifier visualEffect) {
        return goCard(stackModel.getCardCount() - 1, visualEffect, true);
    }

    /**
     * Deletes the current card provided there are more than one card in the stack.
     * @return The card now visible in the stack window, or null if the current card could not be deleted.
     */
    public CardPart deleteCard() {

        if (canDeleteCard()) {
            ToolsContext.getInstance().setIsEditingBackground(false);

            int deletedCardIndex = stackModel.getCurrentCardIndex();
            stackModel.deleteCardModel();
            cardCountProvider.onNext(stackModel.getCardCount());
            fireOnCardOrderChanged();

            return activateCard(deletedCardIndex == 0 ? 0 : deletedCardIndex - 1);
        }

        HyperCard.getInstance().showErrorDialog(new HtSemanticException("This card cannot be deleted because it or its background is marked as \"Can't Delete\"."));
        return null;
    }

    /**
     * Creates a new card with a new background. Differs from {@link #newCard()} in that {@link #newCard()} creates a
     * new card with the same background as the current card.
     *
     * @return The newly created card.
     */
    public CardPart newBackground() {
        ToolsContext.getInstance().setIsEditingBackground(false);

        stackModel.newCardWithNewBackground();
        cardCountProvider.onNext(stackModel.getCardCount());
        fireOnCardOrderChanged();

        return goNextCard(null);
    }

    /**
     * Creates a new card with the same background as the current card. See {@link #newBackground()} to create a new
     * card with a new background.
     *
     * @return The newly created card.
     */
    public CardPart newCard() {
        ToolsContext.getInstance().setIsEditingBackground(false);

        stackModel.newCard(currentCard.getCardModel().getBackgroundId());
        cardCountProvider.onNext(stackModel.getCardCount());
        fireOnCardOrderChanged();

        return goNextCard(null);
    }

    /**
     * Removes the current card from the stack and places it into the card clipboard (for pasting elsewhere in the
     * stack).
     */
    public void cutCard() {
        cardClipboardProvider.onNext(Optional.of(getDisplayedCard()));
        cardCountProvider.onNext(stackModel.getCardCount());

        deleteCard();
    }

    /**
     * Copies the displayed card to the card clipboard for pasting elsewhere in the stack.
     */
    public void copyCard() {
        cardClipboardProvider.onNext(Optional.of(getDisplayedCard()));
    }

    /**
     * Adds the card presently held in the card clipboard to the stack in the current card's position. Has no affect
     * if the clipboard is empty.
     */
    public void pasteCard() {
        if (cardClipboardProvider.blockingFirst().isPresent()) {
            ToolsContext.getInstance().setIsEditingBackground(false);

            CardModel card = cardClipboardProvider.blockingFirst().get().getCardModel().copyOf();
            card.relinkParentPartModel(getStackModel());
            card.defineProperty(CardModel.PROP_ID, new Value(getStackModel().getNextCardId()), true);

            stackModel.insertCard(card);
            cardCountProvider.onNext(stackModel.getCardCount());
            fireOnCardOrderChanged();

            goNextCard(null);
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
     */
    public void invalidateCache() {
        this.currentCard = buildCardPart(getStackModel().getCurrentCardIndex());
        this.cardCountProvider.onNext(stackModel.getCardCount());

        fireOnCardOrderChanged();
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
    public void onPropertyChanged(PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case StackModel.PROP_NAME:
                fireOnStackNameChanged(newValue.stringValue());
                break;

            case StackModel.PROP_HEIGHT:
            case StackModel.PROP_WIDTH:
                // Resize the window
                fireOnCardDimensionChanged(getStackModel().getDimension());

                // Re-load the card model into the size
                activateCard(stackModel.getCurrentCardIndex());
                break;

            case StackModel.PROP_RESIZABLE:
                WindowManager.getInstance().getStackWindow().setAllowResizing(newValue.booleanValue());
                break;
        }
    }

    private CardPart buildCardPart(int index) {
        try {
            return CardPart.fromPositionInStack(index, stackModel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    private CardPart go(int cardIndex, boolean push) {
        // Nothing to do if navigating to current card or an invalid card index
        if (cardIndex == stackModel.getCurrentCardIndex() || cardIndex < 0 || cardIndex >= stackModel.getCardCount()) {
            return getDisplayedCard();
        }

        deactivateCard(push);
        return activateCard(cardIndex);
    }

    private void deactivateCard(boolean push) {
        CardPart displayedCard = getDisplayedCard();

        // Deactivate paint tool before doing anything (to commit in-fight changes)
        ToolsContext.getInstance().getPaintTool().deactivate();

        // Stop editing background when card changes
        ToolsContext.getInstance().setIsEditingBackground(false);

        // When requested, push the current card onto the backstack
        if (push) {
            stackModel.getBackStack().push(displayedCard.getId());
        }

        // Notify observers that current card is going away
        fireOnCardClosing(displayedCard);
        displayedCard.partClosed();
    }

    private CardPart activateCard (int cardIndex) {

        try {
            // Change card
            currentCard = buildCardPart(cardIndex);
            stackModel.setCurrentCardIndex(cardIndex);

            // Notify observers of new card
            currentCard.partOpened();
            fireOnCardOpened(currentCard);

            // Reactive paint tool on new card's canvas
            ToolsContext.getInstance().reactivateTool(currentCard.getCanvas());

            return currentCard;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    private boolean canDeleteCard() {
        long cardCountInBackground = stackModel.getCardsInBackground(getDisplayedCard().getCardModel().getBackgroundId()).size();

        return stackModel.getCardCount() > 1 &&
                !getDisplayedCard().getCardModel().getKnownProperty(CardModel.PROP_CANTDELETE).booleanValue() &&
                (cardCountInBackground > 1 || !getDisplayedCard().getCardModel().getBackgroundModel().getKnownProperty(BackgroundModel.PROP_CANTDELETE).booleanValue());
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
