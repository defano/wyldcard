/*
 * StackModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.model;

import java.awt.*;
import java.util.*;
import java.util.List;

public class StackModel extends PropertiesModel {

    private final static StackModel instance = new StackModel();

    private String name;
    private int nextPartId = 0;
    private int nextCardId = 0;
    private int nextBackgroundId = 0;
    private int currentCardIndex = 0;
    private Stack<Integer> backStack = new Stack<>();
    private int width = 640;
    private int height = 480;
    private final List<CardModel> cardModels;
    private final Map<Integer, BackgroundModel> backgroundModels;

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
        stack.cardModels.add(CardModel.emptyCardModel(stack.getNextCardId(), stack.newBackgroundModel()));
        stack.name = name;
        return stack;
    }

    public int insertCard(CardModel cardModel) {
        cardModels.add(currentCardIndex + 1, cardModel);
        return currentCardIndex + 1;
    }

    public int newCard(int backgroundId) {
        return insertCard(CardModel.emptyCardModel(getNextCardId(), backgroundId));
    }

    public int newCardWithNewBackground() {
        return insertCard(CardModel.emptyCardModel(getNextCardId(), newBackgroundModel()));
    }

    private int newBackgroundModel() {
        int newBackgroundId = getNextBackgroundId();
        backgroundModels.put(newBackgroundId, BackgroundModel.emptyBackground());
        return newBackgroundId;
    }

    public void deleteCardModel() {
        cardModels.remove(currentCardIndex);
    }

    public String getStackName() {
        return this.name;
    }

    public List<CardModel> getCardModels() {
        return cardModels;
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

    public int getNextButtonId() {
        return nextPartId++;
    }

    public int getNextFieldId() {
        return nextPartId++;
    }

    public int getNextCardId() {
        return nextCardId++;
    }

    public int getNextBackgroundId() {
        return nextBackgroundId++;
    }
}
