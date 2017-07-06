/*
 * StackModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.Provider;

import java.awt.*;
import java.util.*;
import java.util.List;

public class StackModel {

    private final static StackModel instance = new StackModel();

    private String name;
    private int currentCardIndex = 0;
    private Stack<Integer> backStack = new Stack<>();
    private int width = 640;
    private int height = 480;
    private final List<CardModel> cardModels;
    private final Map<Integer, BackgroundModel> backgroundModels;
    private final Provider<Integer> cardCountProvider = new Provider<>(0);

    public static StackModel getInstance() {
        return instance;
    }

    private StackModel() {
        this.cardModels = new ArrayList<>();
        this.backgroundModels = new HashMap<>();
        this.backStack = new Stack<>();
    }

    public static StackModel newStackModel(String name) {
        StackModel stack = new StackModel();
        stack.cardModels.add(CardModel.emptyCardModel());
        stack.backgroundModels.put(0, BackgroundModel.emptyBackground());
        stack.name = name;
        stack.cardCountProvider.set(stack.cardModels.size());
        return stack;
    }

    public void newCardModel() {
        cardModels.add(currentCardIndex + 1, CardModel.emptyCardModel());
        cardCountProvider.set(cardModels.size());
    }

    public void deleteCardModel() {
        cardModels.remove(currentCardIndex);
        cardCountProvider.set(cardModels.size());
    }

    public String getStackName() {
        return this.name;
    }

    public CardModel getCardModel(int index) {
        return cardModels.get(index);
    }

    public int getCardCount() {
        return cardModels.size();
    }

    public int getCurrentCardIndex() {
        return currentCardIndex;
    }

    public void setCurrentCardIndex(int currentCard) {
        this.currentCardIndex = currentCard;
    }

    public int getIndexOfCard(CardModel card) {
        return cardModels.indexOf(card);
    }

    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BackgroundModel getBackground(int backgroundId) {
        return backgroundModels.get(backgroundId);
    }

    public Stack<Integer> getBackStack() {
        return backStack;
    }

    public ImmutableProvider<Integer> getCardCountProvider() {
        return ImmutableProvider.from(cardCountProvider);
    }
}
