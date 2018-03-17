package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.menu.HyperCardMenuBar;
import io.reactivex.Observable;

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
    @RunOnDispatch
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
    Observable<Boolean> getWindowVisibleProvider();

    /**
     * Specifies the default button in this window (i.e., the button that will be hilited and which activates when the
     * return/enter key is pressed. Override in window subclasses to specify a default button.
     *
     * @return The default button on this window, or null if no button is a default.
     */
    @RunOnDispatch
    default JButton getDefaultButton() {
        return null;
    }

    @RunOnDispatch
    default void setContentPane(Container contentPane) {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setContentPane(contentPane);
        } else if (getWindow() instanceof JFrame) {
            ((JFrame) getWindow()).setContentPane(contentPane);
        }
    }

    @RunOnDispatch
    default void setDefaultCloseOperation(int operation) {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setDefaultCloseOperation(operation);
        } else if (getWindow() instanceof JFrame) {
            ((JFrame) getWindow()).setDefaultCloseOperation(operation);
        }
    }

    @RunOnDispatch
    default void setTitle(String title) {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setTitle(title);
        } else if (getWindow() instanceof JFrame) {
            ((JFrame) getWindow()).setTitle(title);
        }
    }

    @RunOnDispatch
    default void setAllowResizing(boolean resizable) {
        if (getWindow() instanceof JFrame) {
            ((JFrame) getWindow()).setResizable(resizable);
        } if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setResizable(resizable);
        }
    }

    @RunOnDispatch
    default void applyMenuBar() {
        if (getWindow() instanceof JFrame) {
            if (ownsMenubar() || WindowManager.getInstance().isMacOs()) {
                ((JFrame) getWindow()).setJMenuBar(HyperCardMenuBar.instance);
            } else {
                ((JFrame) getWindow()).setJMenuBar(null);
            }
        }
    }

    @RunOnDispatch
    default void toggleVisible() {
        getWindow().setVisible(!getWindow().isVisible());
    }

    @RunOnDispatch
    default void setIsModal() {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        } else {
            throw new IllegalStateException("This kind of window cannot be made modal");
        }
    }
}
