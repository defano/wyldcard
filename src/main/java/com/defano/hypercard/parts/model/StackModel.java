/*
 * StackModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.CardPart;

import java.util.*;

public class StackModel {

    private String name;
    private int currentCard;
    private Stack<Integer> backStack;
    private int width = 640;
    private int height = 480;

    private final List<CardModel> cards;
    private final Map<Integer, BackgroundModel> backgrounds;

    private transient List<StackModelObserver> observers;
    private transient CardPart currentCardPart;

    private StackModel () {
        this.observers = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.backgrounds = new HashMap<>();
        this.backStack = new Stack<>();
    }

    public static StackModel newStack (String name) {
        StackModel stack = new StackModel();
        stack.cards.add(CardModel.emptyCardModel());
        stack.backgrounds.put(0, BackgroundModel.emptyBackground());
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
            currentCardPart = CardPart.fromModel(cards.get(currentCard), this);
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

        // Stop editing background when card changes
        ToolsContext.getInstance().setIsEditingBackground(false);

        fireOnCardClosing(currentCardPart);

        // When requested, push the current card onto the backstack
        if (push) {
            backStack.push(currentCard);
        }

        try {
            currentCard = card;
            currentCardPart = CardPart.fromModel(cards.get(currentCard), this);

            fireOnCardOpening(currentCardPart);

            return currentCardPart;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card.", e);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BackgroundModel getBackground(int backgroundId) {
        return backgrounds.get(backgroundId);
    }

    private void fireOnCardClosing (CardPart closingCard) {
        for (StackModelObserver observer : observers) {
            observer.onCardOpening(closingCard);
        }
    }

    private void fireOnCardOpening (CardPart openingCard) {
        for (StackModelObserver observer : observers) {
            observer.onCardOpening(openingCard);
        }
    }

    public void fireOnCardOpened (CardPart openedCard) {
        for (StackModelObserver observer : observers) {
            observer.onCardOpening(openedCard);
        }
    }

}
