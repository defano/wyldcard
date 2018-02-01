package com.defano.hypercard.window;

import com.defano.hypercard.fx.CurtainManager;
import com.defano.hypercard.fx.CurtainObserver;
import com.defano.hypercard.paint.ArtVandelay;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.stack.ScreenCurtain;
import com.defano.hypercard.parts.stack.StackNavigationObserver;
import com.defano.hypercard.parts.stack.StackObserver;
import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.util.FileDrop;
import com.defano.hypercard.util.ThreadUtils;
import com.defano.hypercard.util.Throttle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class StackWindow extends HyperCardFrame implements StackObserver, StackNavigationObserver, CurtainObserver {

    private final static int CARD_LAYER = 0;
    private final static int CURTAIN_LAYER = 1;

    private StackPart stack;
    private CardPart card;

    private final JLayeredPane cardPanel = new JLayeredPane();
    private final ScreenCurtain screenCurtain = new ScreenCurtain();
    private final CardResizeObserver cardResizeObserver = new CardResizeObserver();
    private final FrameResizeObserver frameResizeObserver = new FrameResizeObserver();

    public StackWindow() {
        ThreadUtils.assertDispatchThread();

        cardPanel.setLayout(new BorderLayout(0, 0));
        cardPanel.setLayer(screenCurtain, CURTAIN_LAYER);
        cardPanel.add(screenCurtain);

        CurtainManager.getInstance().addScreenCurtainObserver(this);
    }

    public CardPart getDisplayedCard() {
        return card;
    }

    public void invalidateWindowTitle() {
        ThreadUtils.assertDispatchThread();

        // Don't update title when screen is locked
        if (screenCurtain.isVisible()) {
            return;
        }

        String stackName = card.getCardModel().getStackModel().getStackName();
        int cardNumber = card.getCardModel().getCardIndexInStack() + 1;
        int cardCount = stack.getCardCountProvider().blockingFirst();

        if (card.isForegroundHidden()) {
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
        ThreadUtils.assertDispatchThread();

        if (data instanceof StackPart) {
            this.stack = (StackPart) data;
            this.card = stack.getDisplayedCard();

            getWindowPanel().setPreferredSize(stack.getStackModel().getSize());

            stack.addObserver(this);
            stack.addNavigationObserver(this);
        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window." + data);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStackOpened(StackPart newStack) {
        ThreadUtils.assertDispatchThread();

        stack = newStack;
        card = stack.getDisplayedCard();

        cardPanel.setPreferredSize(stack.getStackModel().getSize());
        setAllowResizing(stack.getStackModel().isResizable());

        cardPanel.addComponentListener(cardResizeObserver);
        getWindow().addComponentListener(frameResizeObserver);
    }

    /** {@inheritDoc} */
    @Override
    public void onCardOpened(CardPart newCard) {
        ThreadUtils.assertDispatchThread();

        this.card = newCard;

        // Listen for image files that are dropped onto the card
        new FileDrop(card, ArtVandelay::importPaint);

        for (Component c : cardPanel.getComponentsInLayer(CARD_LAYER)) {
            cardPanel.remove(c);
        }

        cardPanel.setLayer(card, CARD_LAYER);
        cardPanel.add(card);

        cardPanel.revalidate();
        cardPanel.repaint();

        invalidateWindowTitle();
    }

    /** {@inheritDoc} */
    @Override
    public void onStackDimensionChanged(Dimension newDimension) {
        ThreadUtils.assertDispatchThread();

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
    public void onCurtainUpdated(BufferedImage curtainImage) {
        this.screenCurtain.setCurtainImage(curtainImage);

        // Refresh title when unlocking screen
        if (curtainImage == null) {
            invalidateWindowTitle();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCardOrderChanged() {
        invalidateWindowTitle();
    }

    private class CardResizeObserver extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            screenCurtain.setSize(e.getComponent().getSize());
        }
    }

    private class FrameResizeObserver extends ComponentAdapter {
        private final Throttle resizeThrottle = new Throttle(500);

        @Override
        public void componentResized(ComponentEvent e) {
            resizeThrottle.submitOnUiThread(() -> {
                Insets windowInsets = getWindow().getInsets();

                int newCardHeight = getWindow().getHeight() - windowInsets.top - windowInsets.bottom;
                int newCardWidth = getWindow().getWidth() - windowInsets.left - windowInsets.right;

                if (stack.getStackModel().isResizable()) {
                    stack.getStackModel().setDimension(new Dimension(newCardWidth, newCardHeight));
                }
            });
        }
    }
}
