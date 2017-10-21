package com.defano.hypercard.awt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public interface MouseMotionListenable extends MouseMotionListener {

    @Override
    default void mouseDragged(MouseEvent e) {}

    @Override
    default void mouseMoved(MouseEvent e) {}
}
