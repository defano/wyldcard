package com.defano.wyldcard.awt.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * A mix-in providing default (empty) implementations of methods defined in {@link MouseMotionListener}.
 */
public interface MouseMotionListenable extends MouseMotionListener {

    @Override
    default void mouseDragged(MouseEvent e) {}

    @Override
    default void mouseMoved(MouseEvent e) {}
}
