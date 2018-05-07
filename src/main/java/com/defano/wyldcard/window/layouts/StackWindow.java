package com.defano.wyldcard.window.layouts;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.fx.CurtainObserver;
import com.defano.wyldcard.paint.ArtVandelay;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.stack.ScreenCurtain;
import com.defano.wyldcard.parts.stack.StackNavigationObserver;
import com.defano.wyldcard.parts.stack.StackObserver;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.util.FileDrop;
import com.defano.wyldcard.util.Throttle;
import com.defano.wyldcard.window.WyldCardWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class StackWindow extends WyldCardWindow implements StackObserver, StackNavigationObserver, CurtainObserver {

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
    }

    public CardPart getDisplayedCard() {
        return card;
    }

    public StackPart getStack() {
        return stack;
    }

    @RunOnDispatch
    public void invalidateWindowTitle() {

        // Don't update title when screen is locked
        if (screenCurtain.isVisible()) {
            return;
        }

        String stackName = card.getCardModel().getStackModel().getStackName(new ExecutionContext());
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
        ExecutionContext context = new ExecutionContext();

       if (data instanceof StackPart) {

            if (this.stack != null) {
                stack.removeObserver(this);
                stack.removeNavigationObserver(this);
            }

            this.stack = (StackPart) data;
            this.card = stack.getDisplayedCard();

            getWindowPanel().setPreferredSize(stack.getStackModel().getSize(context));

            stack.addObserver(this);
            stack.addNavigationObserver(this);
            stack.getCurtainManager().addScreenCurtainObserver(this);

        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window." + data);
        }
    }

    /** {@inheritDoc} */
    @Override
    @RunOnDispatch
    public void onStackOpened(StackPart newStack) {
        ExecutionContext context = new ExecutionContext();

        stack = newStack;
        card = stack.getDisplayedCard();

        cardPanel.setPreferredSize(stack.getStackModel().getSize(context));
        setAllowResizing(stack.getStackModel().isResizable(context));

        cardPanel.addComponentListener(cardResizeObserver);
        getWindow().addComponentListener(frameResizeObserver);
        getWindow().addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                WyldCard.getInstance().focusStack(stack);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                // Nothing to do
            }
        });
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

                if (stack.getStackModel().isResizable(new ExecutionContext())) {
                    stack.getStackModel().setDimension(new ExecutionContext(), new Dimension(newCardWidth, newCardHeight));
                }
            });
        }
    }
}
