package com.defano.wyldcard.window.layout;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.effect.CurtainObserver;
import com.defano.wyldcard.paint.ArtVandelay;
import com.defano.wyldcard.part.card.CardModel;
import com.defano.wyldcard.part.card.CardPart;
import com.defano.wyldcard.part.stack.ScreenCurtain;
import com.defano.wyldcard.part.stack.StackNavigationObserver;
import com.defano.wyldcard.part.stack.StackObserver;
import com.defano.wyldcard.part.stack.StackPart;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Throttle;
import com.defano.wyldcard.util.FileDrop;
import com.defano.wyldcard.window.WyldCardWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;

public class StackWindow extends WyldCardWindow<StackPart> implements StackObserver, StackNavigationObserver, CurtainObserver {

    private final static int CARD_LAYER = 0;
    private final static int CURTAIN_LAYER = 1;

    private final JLayeredPane cardPanel = new JLayeredPane();
    private final ScreenCurtain screenCurtain = new ScreenCurtain();
    private final CardResizeObserver cardResizeObserver = new CardResizeObserver();
    private final FrameResizeObserver frameResizeObserver = new FrameResizeObserver();
    private final WindowMovementObserver windowMovementObserver = new WindowMovementObserver();
    private final StackFocusObserver stackFocusObserver = new StackFocusObserver();

    private StackPart displayedStack;       // Stack displayed in window
    private CardPart displayedCard;         // Card of stack displayed in window

    public StackWindow() {
        cardPanel.setLayout(new BorderLayout(0, 0));
        cardPanel.setLayer(screenCurtain, CURTAIN_LAYER);
        cardPanel.add(screenCurtain);
    }

    public CardPart getDisplayedCard() {
        return displayedCard;
    }

    public StackPart getDisplayedStack() {
        return displayedStack;
    }

    @RunOnDispatch
    public void invalidateWindowTitle() {

        // Don't update title when screen is locked
        if (screenCurtain.isVisible()) {
            return;
        }

        String stackName = displayedCard.getPartModel().getStackModel().getStackName(new ExecutionContext());
        int cardNumber = displayedCard.getPartModel().getCardIndexInStack() + 1;
        int cardCount = displayedStack.getCardCountProvider().blockingFirst();

        if (displayedCard.isEditingBackground()) {
            getWindow().setTitle(stackName + " - Card " + cardNumber + " of " + cardCount + " (Background)");
        } else {
            getWindow().setTitle(stackName + " - Card " + cardNumber + " of " + cardCount);
        }
    }

    public ScreenCurtain getScreenCurtain() {
        return screenCurtain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getWindowPanel() {
        return cardPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void bindModel(StackPart data) {
        ExecutionContext context = new ExecutionContext();

        if (this.displayedStack != null) {
            displayedStack.removeObserver(this);
            displayedStack.removeNavigationObserver(this);
            displayedStack.getCurtainManager().removeScreenCurtainObserver(this);
        }

        this.displayedStack = data;
        this.displayedCard = displayedStack.getDisplayedCard();

        getWindowPanel().setPreferredSize(displayedStack.getStackModel().getSize(context));

        displayedStack.addObserver(this);
        displayedStack.addNavigationObserver(this);
        displayedStack.getCurtainManager().addScreenCurtainObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onStackOpened(StackPart openedStack) {
        ExecutionContext context = new ExecutionContext();

        displayedStack = openedStack;
        displayedCard = displayedStack.getDisplayedCard();

        cardPanel.setPreferredSize(displayedStack.getStackModel().getSize(context));
        setAllowResizing(displayedStack.getStackModel().isResizable(context));

        Point position = openedStack.getPartModel().getWindowPosition();
        if (position != null) {
            positionWindow(position.x, position.y);
        }

        cardPanel.addComponentListener(cardResizeObserver);
        getWindow().addComponentListener(frameResizeObserver);
        getWindow().addComponentListener(windowMovementObserver);
        getWindow().addWindowFocusListener(stackFocusObserver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onStackClosed(StackPart closedStack) {
        cardPanel.removeComponentListener(cardResizeObserver);
        getWindow().removeComponentListener(frameResizeObserver);
        getWindow().removeComponentListener(windowMovementObserver);
        getWindow().removeWindowFocusListener(stackFocusObserver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onDisplayedCardChanged(CardModel prevCard, CardPart nextCard) {

        // Remove the current card from the window
        cardPanel.remove(displayedCard);

        this.displayedCard = nextCard;

        // Listen for image files that are dropped onto the card
        new FileDrop(displayedCard, ArtVandelay::importPaint);

        cardPanel.setLayer(displayedCard, CARD_LAYER);
        cardPanel.add(displayedCard);
        cardPanel.revalidate(); cardPanel.repaint();

        invalidateWindowTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onStackDimensionChanged(Dimension newDimension) {
        getWindowPanel().setPreferredSize(newDimension);
        getWindow().pack();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onStackNameChanged(String newName) {
        invalidateWindowTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onCurtainUpdated(BufferedImage curtainImage) {
        this.screenCurtain.setCurtainImage(curtainImage);

        // Refresh title when unlocking screen
        if (curtainImage == null) {
            invalidateWindowTitle();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onCardOrderChanged() {
        invalidateWindowTitle();
    }

    private class StackFocusObserver implements WindowFocusListener {
        @Override
        public void windowGainedFocus(WindowEvent e) {
            WyldCard.getInstance().getStackManager().focusStack(displayedStack);
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            // Nothing to do
        }
    }

    private class WindowMovementObserver extends ComponentAdapter {
        @Override
        public void componentMoved(ComponentEvent e) {
            if (displayedStack != null) {
                displayedStack.getPartModel().setWindowPosition(new Value(getLocation()));
            }
        }
    }

    private class CardResizeObserver extends ComponentAdapter {
        @Override
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

                if (displayedStack.getStackModel().isResizable(new ExecutionContext())) {
                    displayedStack.getStackModel().setDimension(new ExecutionContext(), new Dimension(newCardWidth, newCardHeight));
                }
            });
        }
    }
}
