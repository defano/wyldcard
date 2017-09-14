package com.defano.hypercard.awt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * A mixin providing a default implementation of the {@link MouseListener} API for classes that wish to listen
 * to mouse events but don't need all the callbacks and cannot extend from {@link java.awt.event.MouseAdapter}.
 */
public interface MouseListenable extends MouseListener {
    @Override
    default void mouseClicked(MouseEvent e) {}

    @Override
    default void mousePressed(MouseEvent e) {}

    @Override
    default void mouseReleased(MouseEvent e) {}

    @Override
    default void mouseEntered(MouseEvent e) {}

    @Override
    default void mouseExited(MouseEvent e) {}
}
