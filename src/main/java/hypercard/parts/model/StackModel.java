package hypercard.parts.model;

import hypercard.parts.CardPart;

import java.util.ArrayList;
import java.util.List;

public class StackModel {

    private final List<CardModel> cards;
    private int currentCard;
    private String name;

    private transient List<StackModelObserver> observers;

    private StackModel () {
        this.observers = new ArrayList<>();
        this.cards = new ArrayList<>();
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

    public CardPart getCurrentCard() {
        try {
            return CardPart.fromModel(cards.get(currentCard));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    public CardPart goNextCard() {
        if (currentCard + 1 < cards.size()) {
            return setCurrentCard(currentCard + 1);
        } else {
            return null;
        }
    }

    public CardPart goPrevCard() {
        if (currentCard - 1 >= 0) {
            return setCurrentCard(currentCard - 1);
        } else {
            return null;
        }
    }

    public CardPart goFirstCard() {
        return setCurrentCard(0);
    }

    public CardPart goLastCard() {
        return setCurrentCard(cards.size() - 1);
    }

    public CardPart newCard () {
        cards.add(currentCard, CardModel.emptyCardModel());
        return setCurrentCard(currentCard++);
    }

    private CardPart setCurrentCard (int currentCard) {
        this.currentCard = currentCard;
        CardPart selectedCard = getCurrentCard();
        fireOnCurrentCardChanged(selectedCard);
        return selectedCard;
    }

    private void fireOnCurrentCardChanged (CardPart currentCard) {
        for (StackModelObserver observer : observers) {
            observer.onCurrentCardChanged(currentCard);
        }
    }
}
