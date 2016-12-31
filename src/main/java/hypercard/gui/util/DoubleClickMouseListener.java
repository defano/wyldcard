package hypercard.gui.util;

import java.awt.event.*;

public interface DoubleClickMouseListener extends java.awt.event.MouseListener {

    void onDoubleClick(MouseEvent e);

    @Override
    default void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            onDoubleClick(e);
        }
    }

    default void mousePressed(MouseEvent e) {}
    default void mouseReleased(MouseEvent e) {}
    default void mouseEntered(MouseEvent e) {}
    default void mouseExited(MouseEvent e) {}
}
