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

    private static final int CARD_LAYER = 0;
    private static final int CURTAIN_LAYER = 1;

    private final JLayeredPane cardPanel = new JLayeredPane();
    private final ScreenCurtain screenCurtain = new ScreenCurtain();
    private final CardResizeObserver cardResizeObserver = new CardResizeObserver();
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

    public void invalidateWindowSize(ExecutionContext context) {

        cardPanel.setPreferredSize(displayedStack.getStackModel().getSize(context));
        setAllowResizing(displayedStack.getStackModel().isResizable(context));

        Insets insets = getWindow().getInsets();
        getWindow().setMaximumSize(new Dimension(insets.left + insets.right + displayedCard.getWidth(),
                insets.top + insets.bottom + displayedCard.getHeight()));

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

        invalidateWindowSize(context);

        Point position = openedStack.getPartModel().getWindowPosition();
        if (position != null) {
            positionWindow(position.x, position.y);
        }

        cardPanel.addComponentListener(cardResizeObserver);
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
        getWindow().removeComponentListener(windowMovementObserver);
        getWindow().removeWindowFocusListener(stackFocusObserver);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onDisplayedCardChanged(CardModel prevCard, CardPart nextCard) {

        // Listen for image files that are dropped onto the card
        new FileDrop(displayedCard, ArtVandelay::importPaint);

        // Do this last; let card fully initialize/render itself before displaying in the window
        SwingUtilities.invokeLater(() -> {
            // Remove the current card from the window
            cardPanel.remove(displayedCard);

            this.displayedCard = nextCard;

            cardPanel.setLayer(displayedCard, CARD_LAYER);
            cardPanel.add(displayedCard);
            cardPanel.revalidate(); cardPanel.repaint();

            invalidateWindowTitle();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RunOnDispatch
    public void onStackDimensionChanged(Dimension newDimension) {
        getWindowPanel().setPreferredSize(newDimension);
        getWindowPanel().setMaximumSize(newDimension);
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
}
