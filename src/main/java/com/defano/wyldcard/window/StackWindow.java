package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.fx.CurtainManager;
import com.defano.wyldcard.fx.CurtainObserver;
import com.defano.wyldcard.paint.ArtVandelay;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.ScreenCurtain;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;
import com.defano.wyldcard.parts.stack.StackObserver;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.util.FileDrop;
import com.defano.wyldcard.util.ThreadUtils;
import com.defano.wyldcard.util.Throttle;

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
        cardPanel.setLayout(new BorderLayout(0, 0));
        cardPanel.setLayer(screenCurtain, CURTAIN_LAYER);
        cardPanel.add(screenCurtain);

        CurtainManager.getInstance().addScreenCurtainObserver(this);
    }

    public CardPart getDisplayedCard() {
        return card;
    }

    @RunOnDispatch
    public void invalidateWindowTitle() {
        // Don't update title when screen is locked or before card is loaded
        if (screenCurtain.isVisible() || card == null) {
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
    @RunOnDispatch
    public void bindModel(Object data) {
        if (data instanceof StackPart) {

            if (this.stack != null) {
                stack.removeObserver(this);
                stack.removeNavigationObserver(this);
            }

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
    @RunOnDispatch
    public void onStackOpened(StackPart newStack) {
        stack = newStack;
        card = stack.getDisplayedCard();

        cardPanel.setPreferredSize(stack.getStackModel().getSize());
        setAllowResizing(stack.getStackModel().isResizable());

        cardPanel.addComponentListener(cardResizeObserver);
        getWindow().addComponentListener(frameResizeObserver);
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public void onCardOpened(CardPart newCard) {
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
    @RunOnDispatch
    public void onStackDimensionChanged(Dimension newDimension) {
        getWindowPanel().setPreferredSize(newDimension);
        getWindow().pack();
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public void onStackNameChanged(String newName) {
        invalidateWindowTitle();
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public void onCurtainUpdated(BufferedImage curtainImage) {
        this.screenCurtain.setCurtainImage(curtainImage);

        // Refresh title when unlocking screen
        if (curtainImage == null) {
            invalidateWindowTitle();
        }
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public void onCardOrderChanged() {
        invalidateWindowTitle();
    }

    private class CardResizeObserver extends ComponentAdapter {
        @Override
        @RunOnDispatch
        public void componentResized(ComponentEvent e) {
            screenCurtain.setSize(e.getComponent().getSize());
        }
    }

    private class FrameResizeObserver extends ComponentAdapter {
        private final Throttle resizeThrottle = new Throttle("window-resize-throttle", 500);

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
