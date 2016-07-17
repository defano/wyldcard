package hypercard.gui;

import javax.swing.*;

public interface HyperCardWindow {
    JPanel getWindowPanel();

    void bindModel(Object data);

    default void close () {
        SwingUtilities.getWindowAncestor(getWindowPanel()).dispose();
    }
}
