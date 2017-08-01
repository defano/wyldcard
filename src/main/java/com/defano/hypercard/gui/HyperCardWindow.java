package com.defano.hypercard.gui;

import javax.swing.*;
import java.awt.*;

public interface HyperCardWindow<WindowType extends Window> {

    /**
     * Gets the contents of this window; the root component (usually a JPanel) that contains all of the Swing elements
     * present in this window.
     *
     * @return The window contents
     */
    JComponent getWindowPanel();

    /**
     * Update the contents of the window with the given model data.
     * @param data An object representing the data to be displayed in the window.
     */
    void bindModel(Object data);

    /**
     * Close and dispose the window.
     */
    default void dispose() {
        SwingUtilities.getWindowAncestor(getWindowPanel()).dispose();
    }

    /**
     * Gets the AWT window object that is bound to this application window (e.g., a JFrame or JDialog).
     * @return The window object.
     */
    WindowType getWindow();

    /**
     * Sets the AWT window object that is bound this application window.
     * @param windowFrame The window object
     */
    void setWindow(WindowType windowFrame);
}
