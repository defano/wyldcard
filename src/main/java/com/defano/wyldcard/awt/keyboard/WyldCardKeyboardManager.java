package com.defano.wyldcard.awt.keyboard;

import com.defano.jmonet.canvas.JMonetCanvas;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.part.stack.StackModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.google.inject.Singleton;

import javax.swing.FocusManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides an AWT-level view of keyboard events; see {@link RoboticTypist} for a utility to produce keystroke events
 * from a String.
 */
@Singleton
public class WyldCardKeyboardManager implements KeyboardManager {

    private final Set<KeyListener> observers = new HashSet<>();

    private boolean isShiftDown;
    private boolean isAltOptionDown;          // Either 'alt' or 'option' (Mac)
    private boolean isCtrlCommandDown;        // Either 'ctrl' or 'command' (Mac)
    private Long breakTime;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {

            isShiftDown = e.isShiftDown();
            isAltOptionDown = e.isAltDown();
            isCtrlCommandDown = e.isMetaDown() || e.isControlDown();

            if (e.getKeyCode() == KeyEvent.VK_PERIOD && isCtrlCommandDown) {
                breakTime = System.currentTimeMillis();
            }

            fireGlobalKeyListeners(e);

            // When no UI element has keyboard focus, send key press events to the displayed card
            if (FocusManager.getCurrentManager().getFocusOwner() == null || FocusManager.getCurrentManager().getFocusOwner() instanceof JMonetCanvas) {
                delegateKeyEventToFocusedCard(e);
            }

            return false;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addGlobalKeyListener(KeyListener observer) {
        observers.add(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGlobalKeyListener(KeyListener observer) {
        return observers.remove(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getBreakTime() {
        return breakTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShiftDown() {
        return isShiftDown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAltOptionDown() {
        return isAltOptionDown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCtrlCommandDown() {
        return isCtrlCommandDown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPeeking(ExecutionContext context) {
        return isAltOptionDown() && isCtrlCommandDown() &&
                !WyldCard.getInstance().getStackManager().getFocusedStack()
                        .getStackModel()
                        .get(context, StackModel.PROP_CANTPEEK)
                        .booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetKeyStates() {
        isShiftDown = false;
        isAltOptionDown = false;
        isCtrlCommandDown = false;
    }

    private void fireGlobalKeyListeners(KeyEvent e) {
        Set<KeyListener> listeners = new HashSet<>(observers);

        for (KeyListener thisListener : listeners) {
            switch (e.getID()) {
                case KeyEvent.KEY_PRESSED:
                    thisListener.keyPressed(e);
                    break;
                case KeyEvent.KEY_RELEASED:
                    thisListener.keyReleased(e);
                    break;
                case KeyEvent.KEY_TYPED:
                    thisListener.keyTyped(e);
                    break;
            }
        }
    }

    private void delegateKeyEventToFocusedCard(KeyEvent e) {
        switch (e.getID()) {
            case KeyEvent.KEY_PRESSED:
                WyldCard.getInstance().getStackManager().getFocusedStack().getDisplayedCard().keyPressed(e);
                break;

            case KeyEvent.KEY_TYPED:
                WyldCard.getInstance().getStackManager().getFocusedStack().getDisplayedCard().keyTyped(e);
                break;

            case KeyEvent.KEY_RELEASED:
                WyldCard.getInstance().getStackManager().getFocusedStack().getDisplayedCard().keyReleased(e);
                break;
        }
    }

}
