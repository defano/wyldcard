package com.defano.hypercard.parts;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.Serializer;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.model.StackModel;
import com.defano.hypercard.parts.model.StackObserver;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.Provider;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "virtual" view object of the stack itself. See {@link StackModel} for the data model.
 *
 * Note that while this class represents a view, it has no specific Swing component associated with it. Instead, it
 * represents a stack of cards; the current card is the Swing component which represents the stack's view.
 */
public class StackPart {

    private final static StackPart instance = new StackPart();

    private StackModel stackModel;
    private List<StackObserver> observers;
    private CardPart currentCard;
    private Provider<Integer> cardCountProvider = new Provider<>(0);
    private Provider<CardPart> cardClipboardProvider = new Provider<>();

    private StackPart() {
        this.observers = new ArrayList<>();
        this.stackModel = StackModel.newStackModel("Untitled");
        this.currentCard = getCard(0);
    }

    public static StackPart getInstance() {
        return instance;
    }

    /**
     * Prompts the user to choose a stack file to open.
     */
    public void open() {
        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindowFrame(), "Open Stack", FileDialog.LOAD);
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

        goCard(model.getCurrentCardIndex());
        fireOnStackOpened();
    }

    /**
     * Prompts the user to choose a file in which to save the current stack.
     */
    public void save() {
        FileDialog fd = new FileDialog(WindowManager.getStackWindow().getWindowFrame(), "Save Stack", FileDialog.SAVE);
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
     * Navigates to the given card index. Has no affect if no card with the requested index exists in this stack.
     *
     * Note that card index is zero-based, but card's are numbered starting from one from a user's perspective.
     *
     * @param cardIndex The zero-based index of the card to navigate to.
     * @return The card now visible in the stack window.
     */
    public CardPart goCard(int cardIndex) {
        return go(cardIndex, true);
    }

    /**
     * Navigates to the next card in the stack; has no affect if the current card is the last card.
     * @return The card now visible in the stack window or null if no next card.
     */
    public CardPart goNextCard() {
        if (stackModel.getCurrentCardIndex() + 1 < stackModel.getCardCount()) {
            return go(stackModel.getCurrentCardIndex() + 1, true);
        } else {
            return null;
        }
    }

    /**
     * Naviages to the previous card in the stack; has no affect if the current card is the first card.
     * @return The card now visible in the stack window or null if no previous card.
     */
    public CardPart goPrevCard() {
        if (stackModel.getCurrentCardIndex() - 1 >= 0) {
            return go(stackModel.getCurrentCardIndex() - 1, true);
        } else {
            return null;
        }
    }

    /**
     * Navigates to the last card on the backstack; has no affect if the backstack is empty.
     * @return The card now visible in the stack window, or null if no card available to pop
     */
    public CardPart goBack() {
        if (!stackModel.getBackStack().isEmpty()) {
            return go(stackModel.getBackStack().pop(), false);
        } else {
            return null;
        }
    }

    /**
     * Naviages to the first card in the stack.
     * @return The first card in the stack
     */
    public CardPart goFirstCard() {
        return go(0, true);
    }

    /**
     * Navigates to the last card in the stack.
     * @return The last card in the stack
     */
    public CardPart goLastCard() {
        return go(stackModel.getCardCount() - 1, true);
    }

    /**
     * Deletes the current card provided there are more than one card in the stack.
     * @return The card now visible in the stack window, or null if the current card could not be deleted.
     */
    public CardPart deleteCard() {
        if (stackModel.getCardCount() > 1) {
            ToolsContext.getInstance().setIsEditingBackground(false);

            int deletedCardIndex = stackModel.getCurrentCardIndex();
            stackModel.deleteCardModel();
            cardCountProvider.set(stackModel.getCardCount());

            return activateCard(deletedCardIndex == 0 ? 0 : deletedCardIndex - 1);
        }

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

        return goNextCard();
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

        return goNextCard();
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

            goNextCard();
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
    public ImmutableProvider<Integer> getCardCountProvider() {
        return ImmutableProvider.from(cardCountProvider);
    }

    /**
     * Adds an observer of stack changes.
     * @param observer The observer
     */
    public void addObserver (StackObserver observer) {
        observers.add(observer);
    }

    private void fireOnStackOpened () {
        for (StackObserver observer : observers) {
            observer.onStackOpened(this);
        }
    }

    private void fireOnCardClosing (CardPart closingCard) {
        for (StackObserver observer : observers) {
            observer.onCardClosed(closingCard);
        }
    }

    private void fireOnCardOpened (CardPart openedCard) {
        for (StackObserver observer : observers) {
            observer.onCardOpened(openedCard);
        }
    }

    private CardPart getCard(int index) {
        try {
            return CardPart.fromModel(index, stackModel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    private CardPart go(int cardIndex, boolean addToBackstack) {

        // Nothing to do if navigating to current card or an invalid card index
        if (cardIndex == stackModel.getCurrentCardIndex() || cardIndex < 0 || cardIndex >= stackModel.getCardCount()) {
            return getCurrentCard();
        }

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

        return activateCard(cardIndex);
    }

    private CardPart activateCard (int cardIndex) {
        try {

            // Change cards
            currentCard = getCard(cardIndex);
            stackModel.setCurrentCardIndex(cardIndex);

            // Notify observers of new card
            fireOnCardOpened(currentCard);

            // Reactive paint tool on new card's canvas
            ToolsContext.getInstance().reactivateTool(currentCard.getCanvas());

            return currentCard;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }
}
