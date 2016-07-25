package hypercard.parts.model;

import hypercard.parts.CardPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StackModel {

    private String name;
    private int currentCard;
    private Stack<Integer> backStack;

    private final List<CardModel> cards;

    private transient List<StackModelObserver> observers;
    private transient CardPart currentCardPart;

    private StackModel () {
        this.observers = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.backStack = new Stack<>();
    }

    public static StackModel newStack (String name) {
        StackModel stack = new StackModel();
        stack.cards.add(CardModel.emptyCardModel());
        stack.name = name;
        return stack;
    }

    public void addObserver (StackModelObserver observer) {
        observers.add(observer);
    }

    public String getStackName() {
        return this.name;
    }

    public int getCardCount() {
        return cards.size();
    }

    public CardPart getCurrentCard() {
        if (currentCardPart != null) {
            return currentCardPart;
        }

        try {
            currentCardPart = CardPart.fromModel(cards.get(currentCard));
            return currentCardPart;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    public CardPart goCard(int cardNumber) {
        return setCurrentCard(cardNumber, true);
    }

    public CardPart goNextCard() {
        if (currentCard + 1 < cards.size()) {
            return setCurrentCard(currentCard + 1, true);
        } else {
            return null;
        }
    }

    public CardPart goPrevCard() {
        if (currentCard - 1 >= 0) {
            return setCurrentCard(currentCard - 1, true);
        } else {
            return null;
        }
    }

    public CardPart goBack() {
        if (!backStack.isEmpty()) {
            return setCurrentCard(backStack.pop(), false);
        }

        return null;
    }

    public CardPart goFirstCard() {
        return setCurrentCard(0, true);
    }

    public CardPart goLastCard() {
        return setCurrentCard(cards.size() - 1, true);
    }

    public CardPart newCard () {
        cards.add(currentCard + 1, CardModel.emptyCardModel());
        return setCurrentCard(currentCard + 1, true);
    }

    private CardPart setCurrentCard (int card, boolean push) {

        // Nothing to do if navigating to current card
        if (card == currentCard || card < 0 || card >= cards.size()) {
            return getCurrentCard();
        }

        // When requested, push the current card onto the backstack
        if (push) {
            backStack.push(currentCard);
        }

        try {
            currentCard = card;
            currentCardPart = CardPart.fromModel(cards.get(currentCard));
            fireOnCurrentCardChanged(currentCardPart);
            return currentCardPart;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    private void fireOnCurrentCardChanged (CardPart currentCard) {
        for (StackModelObserver observer : observers) {
            observer.onCurrentCardChanged(currentCard);
        }
    }
}
