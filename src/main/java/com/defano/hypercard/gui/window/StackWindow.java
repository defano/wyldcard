/*
 * StackWindow
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.window;

import com.defano.hypercard.gui.HyperCardFrame;
import com.defano.hypercard.gui.fx.CurtainObserver;
import com.defano.hypercard.gui.fx.CurtainManager;
import com.defano.hypercard.gui.util.FileDrop;
import com.defano.hypercard.gui.util.ImageImporter;
import com.defano.hypercard.parts.CardPart;
import com.defano.hypercard.parts.ScreenCurtain;
import com.defano.hypercard.parts.StackPart;
import com.defano.hypercard.parts.model.StackObserver;
import com.defano.hypercard.parts.util.MouseEventDispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class StackWindow extends HyperCardFrame implements StackObserver, CurtainObserver {

    private final int CARD_LAYER = 0;
    private final int CURTAIN_LAYER = 1;

    private StackPart stack;
    private CardPart card;
    private JLayeredPane cardPanel = new JLayeredPane();
    private ScreenCurtain screenCurtain = new ScreenCurtain();

    public StackWindow() {
        cardPanel.setLayout(new BorderLayout(0, 0));

        cardPanel.add(screenCurtain);
        cardPanel.setLayer(screenCurtain, CURTAIN_LAYER);

        // Keep size of screen curtain the same as this component (can't use a LayoutManager to do this because
        // other components require absolute layout)
        cardPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                screenCurtain.setSize(e.getComponent().getSize());
            }
        });

        // Pass mouse events send to the curtain to card panel behind it
        MouseEventDispatcher.bindTo(screenCurtain, () -> new Component[] {card});

        CurtainManager.getInstance().addScreenCurtainObserver(this);
    }

    public CardPart getDisplayedCard() {
        return card;
    }

    private void displayCard(CardPart card) {
        this.card = card;

        // Listen for image files that are dropped onto the card
        FileDrop fd = new FileDrop(card, files -> ImageImporter.importAsSelection(files[0]));

        for (Component c : cardPanel.getComponentsInLayer(CARD_LAYER)) {
            cardPanel.remove(c);
        }

        cardPanel.add(card);
        cardPanel.setLayer(card, CARD_LAYER);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public void invalidateWindowTitle() {
        String stackName = card.getStackModel().getStackName();
        int cardNumber = card.getCardIndexInStack() + 1;
        int cardCount = stack.getCardCountProvider().get();
        boolean isEditingBackground = !card.isForegroundVisible();

        if (isEditingBackground) {
            getWindow().setTitle(stackName + " - Card " + cardNumber + " of " + cardCount + " (Background)");
        } else {
            getWindow().setTitle(stackName + " - Card " + cardNumber + " of " + cardCount);
        }
    }

    @Override
    public JComponent getWindowPanel() {
        return cardPanel;
    }

    @Override
    public void bindModel(Object data) {
        if (data instanceof StackPart) {
            this.stack = (StackPart) data;
            this.card = this.stack.getCurrentCard();

            this.getWindowPanel().setPreferredSize(this.stack.getStackModel().getSize());
            this.stack.addObserver(this);
        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window." + data);
        }
    }

    @Override
    public void onStackOpened(StackPart newStack) {
        this.stack = newStack;
        this.card = this.stack.getCurrentCard();
        cardPanel.setPreferredSize(this.stack.getStackModel().getSize());

        displayCard(this.stack.getCurrentCard());
        invalidateWindowTitle();
    }

    @Override
    public void onCardClosed(CardPart oldCard) {
        // Nothing to do
    }

    @Override
    public void onCardOpened(CardPart newCard) {
        displayCard(newCard);
        invalidateWindowTitle();
    }

    @Override
    public void onCardDimensionChanged(Dimension newDimension) {
        getWindowPanel().setPreferredSize(newDimension);
        getWindow().pack();
    }

    @Override
    public void onStackNameChanged(String newName) {
        invalidateWindowTitle();
    }

    @Override
    public void onCurtainUpdated(BufferedImage screenCurtain) {
        this.screenCurtain.setCurtainImage(screenCurtain);
    }
}
