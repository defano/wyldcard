package com.defano.hypercard.gui.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A mixin providing a default implementation of the {@link KeyListener} API for classes that wish to listen
 * to key events but don't need all the callbacks and cannot extend from {@link java.awt.event.KeyAdapter}.
 */

public interface KeyListenable extends KeyListener {

    @Override
    default void keyTyped(KeyEvent e) {}

    @Override
    default void keyPressed(KeyEvent e) {}

    @Override
    default void keyReleased(KeyEvent e) {}
}
