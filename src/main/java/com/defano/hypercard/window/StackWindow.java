package com.defano.hypercard.window;

import com.defano.hypercard.fx.CurtainObserver;
import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypercard.paint.ArtVandelay;
import com.defano.hypercard.util.FileDrop;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.stack.ScreenCurtain;
import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.parts.stack.StackObserver;
import com.defano.hypercard.parts.util.MouseEventDispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class StackWindow extends HyperCardFrame implements StackObserver, CurtainObserver {

    private final static int CARD_LAYER = 0;
    private final static int CURTAIN_LAYER = 1;

    private StackPart stack;
    private CardPart card;
    private final JLayeredPane cardPanel = new JLayeredPane();
    private final ScreenCurtain screenCurtain = new ScreenCurtain();

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
        new FileDrop(card, ArtVandelay::importPaint);

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
        boolean isEditingBackground = card.isForegroundHidden();

        if (isEditingBackground) {
            getWindow().setTitle(stackName + " - Card " + cardNumber + " of " + cardCount + " (Background)");
        } else {
            getWindow().setTitle(stackName + " - Card " + cardNumber + " of " + cardCount);
        }
    }

    public ScreenCurtain getScreenCurtain() {
        return screenCurtain;
    }

    /** {@inheritDoc} */
    @Override
    public JComponent getWindowPanel() {
        return cardPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void bindModel(Object data) {
        if (data instanceof StackPart) {
            this.stack = (StackPart) data;
            this.card = this.stack.getDisplayedCard();

            this.getWindowPanel().setPreferredSize(this.stack.getStackModel().getSize());
            this.stack.addObserver(this);
        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window." + data);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStackOpened(StackPart newStack) {
        this.stack = newStack;
        this.card = this.stack.getDisplayedCard();
        cardPanel.setPreferredSize(this.stack.getStackModel().getSize());

        displayCard(this.stack.getDisplayedCard());
        invalidateWindowTitle();
    }

    /** {@inheritDoc} */
    @Override
    public void onCardClosed(CardPart oldCard) {
        // Nothing to do
    }

    /** {@inheritDoc} */
    @Override
    public void onCardOpened(CardPart newCard) {
        displayCard(newCard);
        invalidateWindowTitle();
    }

    /** {@inheritDoc} */
    @Override
    public void onCardDimensionChanged(Dimension newDimension) {
        getWindowPanel().setPreferredSize(newDimension);
        getWindow().pack();
    }

    /** {@inheritDoc} */
    @Override
    public void onStackNameChanged(String newName) {
        invalidateWindowTitle();
    }

    /** {@inheritDoc} */
    @Override
    public void onCurtainUpdated(BufferedImage screenCurtain) {
        this.screenCurtain.setCurtainImage(screenCurtain);
    }

    /** {@inheritDoc} */
    @Override
    public void onCardOrderChanged() {
        invalidateWindowTitle();
    }
}
