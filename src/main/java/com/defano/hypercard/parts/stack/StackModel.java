/*
 * StackModel
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts.stack;

import com.defano.hypercard.icons.ButtonIcon;
import com.defano.hypercard.icons.UserIcon;
import com.defano.hypercard.parts.bkgnd.BackgroundModel;
import com.defano.hypercard.parts.card.CardModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypercard.runtime.serializer.Serializer;
import com.defano.hypertalk.ast.common.Owner;
import com.defano.hypertalk.ast.common.PartType;
import com.defano.hypertalk.ast.common.Value;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class StackModel extends PartModel {

    // Model properties that are not HyperTalk-addressable
    private int nextPartId = 0;
    private int nextCardId = 0;
    private int nextBackgroundId = 0;
    private int currentCardIndex = 0;
    private Stack<Integer> backStack = new Stack<>();
    private final List<CardModel> cardModels;
    private final Map<Integer, BackgroundModel> backgroundModels;
    private final Map<String, byte[]> userIcons;

    private StackModel(String stackName, Dimension dimension) {
        super(PartType.STACK, Owner.HYPERCARD);

        this.cardModels = new ArrayList<>();
        this.backgroundModels = new HashMap<>();
        this.userIcons = new HashMap<>();
        this.backStack = new Stack<>();

        defineProperty(PROP_ID, new Value(0), true);
        defineProperty(PROP_NAME, new Value(stackName), false);
        defineProperty(PROP_WIDTH, new Value(dimension.width), false);
        defineProperty(PROP_HEIGHT, new Value(dimension.height), false);

        defineComputedGetterProperty(PartModel.PROP_LEFT, (model, propertyName) -> new Value(WindowManager.getStackWindow().getWindow().getLocation().x));
        defineComputedSetterProperty(PartModel.PROP_LEFT, (model, propertyName, value) -> WindowManager.getStackWindow().getWindow().setLocation(value.integerValue(), WindowManager.getStackWindow().getWindow().getY()));
        defineComputedGetterProperty(PartModel.PROP_TOP, (model, propertyName) -> new Value(WindowManager.getStackWindow().getWindow().getLocation().y));
        defineComputedSetterProperty(PartModel.PROP_TOP, (model, propertyName, value) -> WindowManager.getStackWindow().getWindow().setLocation(WindowManager.getStackWindow().getWindow().getX(), value.integerValue()));
    }

    public static StackModel newStackModel(String stackName) {
        StackModel stack = new StackModel(stackName, new Dimension(640, 480));
        stack.cardModels.add(CardModel.emptyCardModel(stack.getNextCardId(), stack.newBackgroundModel()));
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
        backgroundModels.put(newBackgroundId, BackgroundModel.emptyBackground(newBackgroundId));
        return newBackgroundId;
    }

    public void deleteCardModel() {
        cardModels.remove(currentCardIndex);
    }

    public String getStackName() {
        return getKnownProperty(PROP_NAME).stringValue();
    }

    public void setStackName(String name) {
        setKnownProperty(PROP_NAME, new Value(name));
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
        return getKnownProperty(PROP_WIDTH).integerValue();
    }

    public int getHeight() {
        return getKnownProperty(PROP_HEIGHT).integerValue();
    }

    public Dimension getDimension() {
        return new Dimension(getWidth(), getHeight());
    }

    public void setDimension(Dimension dimension) {
        setKnownProperty(PROP_WIDTH, new Value(dimension.width));
        setKnownProperty(PROP_HEIGHT, new Value(dimension.height));
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

    public int getBackgroundCount() {
        return backgroundModels.size();
    }

    public long getCardCountInBackground(int backgroundId) {
        return getCardModels()
                .stream()
                .filter(c -> c.getBackgroundId() == backgroundId)
                .count();
    }

    public void createIcon(String name, BufferedImage image) {
        userIcons.put(name, Serializer.serializeImage(image));
    }

    public List<ButtonIcon> getUserIcons() {
        ArrayList<ButtonIcon> icons = new ArrayList<>();
        for (String thisIconName : userIcons.keySet()) {
            icons.add(new UserIcon(thisIconName, userIcons.get(thisIconName)));
        }

        return icons;
    }
}

