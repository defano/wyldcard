package com.defano.hypercard.gui;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.gui.menu.HyperCardMenuBar;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.jmonet.model.Provider;

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

    boolean ownsMenubar();
    void setOwnsMenubar(boolean ownsMenubar);

    Provider<Boolean> getWindowVisibleProvider();

    default void setContentPane(Container contentPane) {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setContentPane(contentPane);
        } else if (getWindow() instanceof JFrame) {
            ((JFrame) getWindow()).setContentPane(contentPane);
        }
    }

    default void setDefaultCloseOperation(int operation) {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setDefaultCloseOperation(operation);
        } else if (getWindow() instanceof JFrame) {
            ((JFrame) getWindow()).setDefaultCloseOperation(operation);
        }
    }

    default void setTitle(String title) {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setTitle(title);
        } else if (getWindow() instanceof JFrame) {
            ((JFrame) getWindow()).setTitle(title);
        }
    }

    default void setResizable(boolean resizable) {
        if (getWindow() instanceof JFrame) {
            ((JFrame) getWindow()).setResizable(resizable);
        } if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setResizable(resizable);
        }
    }

    default void applyMenuBar() {
        if (getWindow() instanceof JFrame) {
            if (ownsMenubar() || WindowManager.isMacOs()) {
                ((JFrame) getWindow()).setJMenuBar(HyperCardMenuBar.instance);
            } else {
                ((JFrame) getWindow()).setJMenuBar(null);
            }
        }
    }

    default void toggleVisible() {
        getWindow().setVisible(!getWindow().isVisible());
    }

    default void setIsModal() {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        } else {
            throw new IllegalStateException("This kind of window cannot be made modal");
        }
    }
}
