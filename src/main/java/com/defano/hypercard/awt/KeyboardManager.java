package com.defano.hypercard.awt;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class KeyboardManager {

    public static boolean isShiftDown;
    public static boolean isAltOptionDown;          // Either 'alt' or 'option' (Mac)
    public static boolean isCtrlCommandDown;        // Either 'ctrl' or 'command' (Mac
    public static boolean isBreakSequence;

    private final static Set<KeyListener> observers = new HashSet<>();

    public static void start() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            isShiftDown = e.isShiftDown();
            isAltOptionDown = e.isAltDown();
            isCtrlCommandDown = e.isMetaDown() || e.isControlDown();

            isBreakSequence = e.getKeyCode() == KeyEvent.VK_PERIOD && isCtrlCommandDown;

            fireGlobalKeyListeners(e);

            return false;
        });
    }

    public static void addGlobalKeyListener(KeyListener observer) {
        observers.add(observer);
    }

    public static boolean removeGlobalKeyListener(KeyListener observer) {
        return observers.remove(observer);
    }

    private static void fireGlobalKeyListeners(KeyEvent e) {
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

}
