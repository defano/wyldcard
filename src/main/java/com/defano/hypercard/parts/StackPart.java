package com.defano.hypercard.parts;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.serializer.Serializer;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.gui.fx.CurtainManager;
import com.defano.hypercard.gui.util.ThreadUtils;
import com.defano.hypercard.parts.model.*;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.common.VisualEffectSpecifier;
import com.defano.jmonet.model.Provider;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "virtual" view object of the stack itself. See {@link StackModel} for the data model.
 * <p>
 * This view is "virtual" because a stack has no view aside from the cards that comprise it. Thus, this class does
 * not extend a Swing component and is not added to a view hierarchy.
 */
public class StackPart implements PropertyChangeObserver, PartContainer {

    private StackModel stackModel;
    private List<StackObserver> observers = new ArrayList<>();
    private CardPart currentCard;
    private Provider<Integer> cardCountProvider = new Provider<>(0);
    private Provider<CardPart> cardClipboardProvider = new Provider<>();

    private StackPart() {}

    public static StackPart fromStackModel(StackModel model) {
        StackPart stackPart = new StackPart();
        stackPart.stackModel = model;

        return stackPart;
    }

    /**
     * Prompts the user to choose a stack file to open.
     */
    public void open() {
        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindow(), "Open Stack", FileDialog.LOAD);
        fd.setVisible(true);
        fd.setMultipleMode(false);
        if (fd.getFiles().length > 0) {
            StackModel model = Serializer.deserialize(fd.getFiles()[0], StackModel.class);
            open(model);
        }
    }

    /**
     * Opens the stack represented by the given model inside the stack window.
     * @param model The data model of the stack to open.
     */
    public void open(StackModel model) {
        this.stackModel = model;
        this.currentCard = getCard(model.getCurrentCardIndex());
        this.cardCountProvider.set(stackModel.getCardCount());

        this.stackModel.addPropertyChangedObserver(this);

        goCard(model.getCurrentCardIndex(), null);
        fireOnStackOpened();
        fireOnCardDimensionChanged(model.getDimension());
        getCurrentCard().openCard();
        fireOnCardOpened(getCurrentCard());
        ToolsContext.getInstance().reactivateTool(currentCard.getCanvas());
    }

    /**
     * Prompts the user to choose a file in which to save the current stack.
     */
    public void save() {
        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindow(), "Save Stack", FileDialog.SAVE);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {

            try {
                Serializer.serialize(fd.getFiles()[0], HyperCard.getInstance().getStack().getStackModel());
            } catch (IOException e) {
                HyperCard.getInstance().showErrorDialog(e);
            }
        }

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
    public CardPart goCard(int cardIndex, VisualEffectSpecifier visualEffect) {
        CardPart destination;

        if (visualEffect == null) {
            destination = go(cardIndex, true);
        } else {
            CurtainManager.getInstance().setScreenLocked(true);
            destination = go(cardIndex, true);
            CurtainManager.getInstance().unlockScreenWithEffect(visualEffect);
        }

        return destination;
    }

    /**
     * Navigates to the next card in the stack; has no affect if the current card is the last card.
     * @return The card now visible in the stack window or null if no next card.
     */
    public CardPart goNextCard(VisualEffectSpecifier visualEffect) {
        if (stackModel.getCurrentCardIndex() + 1 < stackModel.getCardCount()) {
            return goCard(stackModel.getCurrentCardIndex() + 1, visualEffect);
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
            return goCard(stackModel.getCurrentCardIndex() - 1, visualEffect);
        } else {
            return null;
        }
    }

    /**
     * Navigates to the last card on the backstack; has no affect if the backstack is empty.
     * @return The card now visible in the stack window, or null if no card available to pop
     */
    public CardPart goBack(VisualEffectSpecifier visualEffect) {
        if (!stackModel.getBackStack().isEmpty()) {
            return goCard(stackModel.getBackStack().pop(), visualEffect);
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
        return goCard(stackModel.getCurrentCardIndex(), visualEffectSpecifier);
    }

    /**
     * Navigates to the first card in the stack.
     * @return The first card in the stack
     */
    public CardPart goFirstCard(VisualEffectSpecifier visualEffect) {
        return goCard(0, visualEffect);
    }

    /**
     * Navigates to the last card in the stack.
     * @return The last card in the stack
     */
    public CardPart goLastCard(VisualEffectSpecifier visualEffect) {
        return goCard(stackModel.getCardCount() - 1, visualEffect);
    }

    /**
     * Deletes the current card provided there are more than one cards in the stack.
     * @return The card now visible in the stack window, or null if the current card could not be deleted.
     */
    public CardPart deleteCard() {

        if (canDeleteCard()) {
            ToolsContext.getInstance().setIsEditingBackground(false);

            int deletedCardIndex = stackModel.getCurrentCardIndex();
            stackModel.deleteCardModel();
            cardCountProvider.set(stackModel.getCardCount());

            return activateCard(deletedCardIndex == 0 ? 0 : deletedCardIndex - 1);
        }

        HyperCard.getInstance().showErrorDialog(new IllegalStateException("This card cannot be deleted because it or its background is marked as \"Can't Delete\"."));
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
        cardCountProvider.set(stackModel.getCardCount());

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
        cardCountProvider.set(stackModel.getCardCount());

        return goNextCard(null);
    }

    /**
     * Removes the current card from the stack and places it into the card clipboard (for pasting elsewhere in the
     * stack).
     */
    public void cutCard() {
        cardClipboardProvider.set(getCurrentCard());
        cardCountProvider.set(stackModel.getCardCount());

        deleteCard();
    }

    /**
     * Copies the current card to the card clipboard for pasting elsewhere in the stack.
     */
    public void copyCard() {
        cardClipboardProvider.set(getCurrentCard());
    }

    /**
     * Adds the card presently held in the card clipboard to the stack in the current card's position. Has no affect
     * if the clipboard is empty.
     */
    public void pasteCard() {
        if (cardClipboardProvider.get() != null) {
            ToolsContext.getInstance().setIsEditingBackground(false);

            stackModel.insertCard(cardClipboardProvider.get().getCardModel().copyOf());
            cardCountProvider.set(stackModel.getCardCount());

            goNextCard(null);
        }
    }

    /**
     * Gets an observable object containing the contents of the card clipboard.
     * @return The card clipboard provider.
     */
    public Provider<CardPart> getCardClipboardProvider() {
        return cardClipboardProvider;
    }

    /**
     * Gets the currently displayed card.
     * @return The current card
     */
    public CardPart getCurrentCard() {
        if (currentCard == null) {
            currentCard = getCard(getStackModel().getCurrentCardIndex());
        }

        return currentCard;
    }

    /**
     * Gets an observable object containing the number of cards in the stack.
     * @return The card count provider
     */
    public Provider<Integer> getCardCountProvider() {
        return cardCountProvider;
    }

    /**
     * Adds an observer of stack changes.
     * @param observer The observer
     */
    public void addObserver (StackObserver observer) {
        observers.add(observer);
    }

    @Override
    public List<PartModel> getPartsInDisplayOrder() {
        ArrayList<PartModel> cards = new ArrayList<>();
        cards.addAll(stackModel.getCardModels());
        return cards;
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
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
        }
    }

    private void fireOnStackOpened () {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackObserver observer : observers) {
                observer.onStackOpened(StackPart.this);
            }
        });
    }

    private void fireOnCardClosing (CardPart closingCard) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackObserver observer : observers) {
                observer.onCardClosed(closingCard);
            }
        });
    }

    private void fireOnCardOpened (CardPart openedCard) {
        ThreadUtils.invokeAndWaitAsNeeded(() -> {
            for (StackObserver observer : observers) {
                observer.onCardOpened(openedCard);
            }
        });
    }

    private void fireOnCardDimensionChanged(Dimension newDimension) {
        for (StackObserver observer : observers) {
            observer.onCardDimensionChanged(newDimension);
        }
    }

    private void fireOnStackNameChanged(String newName) {
        for (StackObserver observer : observers) {
            observer.onStackNameChanged(newName);
        }
    }

    private CardPart getCard(int index) {
        try {
            return CardPart.fromLocationInStack(index, stackModel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    private CardPart go(int cardIndex, boolean addToBackstack) {
        // Nothing to do if navigating to current card or an invalid card index
        if (cardIndex == stackModel.getCurrentCardIndex() || cardIndex < 0 || cardIndex >= stackModel.getCardCount()) {
            return getCurrentCard();
        }

        deactivateCard(addToBackstack);
        return activateCard(cardIndex);
    }

    private void deactivateCard(boolean addToBackstack) {

        // Deactivate paint tool before doing anything (to commit in-fight changes)
        ToolsContext.getInstance().getPaintTool().deactivate();

        // Stop editing background when card changes
        ToolsContext.getInstance().setIsEditingBackground(false);

        // When requested, push the current card onto the backstack
        if (addToBackstack) {
            stackModel.getBackStack().push(stackModel.getCurrentCardIndex());
        }

        // Notify observers that current card is going away
        fireOnCardClosing(getCurrentCard());
        getCurrentCard().closeCard();
    }

    private CardPart activateCard (int cardIndex) {

        try {
            // Change cards
            currentCard = getCard(cardIndex);
            stackModel.setCurrentCardIndex(cardIndex);

            // Notify observers of new card
            currentCard.openCard();
            fireOnCardOpened(currentCard);

            // Reactive paint tool on new card's canvas
            ToolsContext.getInstance().reactivateTool(currentCard.getCanvas());

            return currentCard;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    private boolean canDeleteCard() {
        long cardCountInBackground = stackModel.getCardCountInBackground(getCurrentCard().getCardModel().getBackgroundId());

        return stackModel.getCardCount() > 1 &&
                !getCurrentCard().getCardModel().getKnownProperty(CardModel.PROP_CANTDELETE).booleanValue() &&
                (cardCountInBackground > 1 || !getCurrentCard().getCardBackground().getKnownProperty(BackgroundModel.PROP_CANTDELETE).booleanValue());
    }

}
