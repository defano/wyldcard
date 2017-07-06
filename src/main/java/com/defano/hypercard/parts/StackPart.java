package com.defano.hypercard.parts;

import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.model.StackModel;
import com.defano.hypercard.parts.model.StackObserver;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.Provider;

import java.util.ArrayList;
import java.util.List;

public class StackPart {

    private final static StackPart instance = new StackPart();

    private StackModel stackModel;
    private List<StackObserver> observers;
    private CardPart currentCard;
    private Provider<Integer> cardCountProvider = new Provider<>(0);


    private StackPart() {
        this.observers = new ArrayList<>();
        this.stackModel = StackModel.newStackModel("Untitled");
        this.currentCard = getCard(0);
    }

    public static StackPart getInstance() {
        return instance;
    }

    public void open(StackModel model) {
        this.stackModel = model;
        this.currentCard = getCard(model.getCurrentCardIndex());
        this.cardCountProvider.set(stackModel.getCardCount());

        goCard(model.getCurrentCardIndex());
        fireOnStackOpened();
    }

    public StackModel getStackModel() {
        return stackModel;
    }

    public CardPart goCard(int cardIndex) {
        return go(cardIndex, true);
    }

    public CardPart goNextCard() {
        if (stackModel.getCurrentCardIndex() + 1 < stackModel.getCardCount()) {
            return go(stackModel.getCurrentCardIndex() + 1, true);
        } else {
            return null;
        }
    }

    public CardPart goPrevCard() {
        if (stackModel.getCurrentCardIndex() - 1 >= 0) {
            return go(stackModel.getCurrentCardIndex() - 1, true);
        } else {
            return null;
        }
    }

    public CardPart goBack() {
        if (!stackModel.getBackStack().isEmpty()) {
            return go(stackModel.getBackStack().pop(), false);
        } else {
            return null;
        }
    }

    public CardPart goFirstCard() {
        return go(0, true);
    }

    public CardPart goLastCard() {
        return go(stackModel.getCardCount() - 1, true);
    }

    public CardPart deleteCard() {
        ToolsContext.getInstance().setIsEditingBackground(false);

        int deletedCardIndex = stackModel.getCurrentCardIndex();
        stackModel.deleteCardModel();
        cardCountProvider.set(stackModel.getCardCount());

        return activateCard(deletedCardIndex == 0 ? 0 : deletedCardIndex - 1);
    }

    public CardPart newCard() {
        ToolsContext.getInstance().setIsEditingBackground(false);

        stackModel.newCardModel();
        cardCountProvider.set(stackModel.getCardCount());

        return goNextCard();
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

    public CardPart getCurrentCard() {
        if (currentCard == null) {
            currentCard = getCard(getStackModel().getCurrentCardIndex());
        }

        return currentCard;
    }

    public ImmutableProvider<Integer> getCardCountProvider() {
        return ImmutableProvider.from(cardCountProvider);
    }

    private CardPart getCard(int index) {
        try {
            return CardPart.fromModel(index, stackModel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

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
}
