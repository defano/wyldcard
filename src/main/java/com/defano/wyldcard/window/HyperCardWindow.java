package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.menu.main.HyperCardMenuBar;
import com.defano.wyldcard.util.ThreadUtils;
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
     *
     * @param data An object representing the data to be displayed in the window.
     */
    void bindModel(Object data);

    /**
     * Close and dispose the window.
     */
    default void dispose() {
        ThreadUtils.invokeAndWaitAsNeeded(() -> SwingUtilities.getWindowAncestor(getWindowPanel()).dispose());
    }

    /**
     * Gets the AWT window object that is bound to this application window (e.g., a JFrame or JDialog).
     *
     * @return The window object.
     */
    WindowType getWindow();

    boolean ownsMenuBar();

    void setOwnsMenuBar(boolean ownsMenuBar);

    default JMenuBar getWyldCardMenuBar() {
        return HyperCardMenuBar.getInstance();
    }

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
    default void positionWindow(int x, int y) {
        DisplayMode mode = getWindow().getGraphicsConfiguration().getDevice().getDisplayMode();

        int xPos = Math.min(x, mode.getWidth() - getWindow().getWidth());
        int yPos = Math.min(y, mode.getHeight() - getWindow().getHeight());
        xPos = Math.max(0, xPos);
        yPos = Math.max(0, yPos);

        getWindow().setLocation(xPos, yPos);
    }

    default void setContentPane(Container contentPane) {
        SwingUtilities.invokeLater(() -> {
            if (getWindow() instanceof JDialog) {
                ((JDialog) getWindow()).setContentPane(contentPane);
            } else if (getWindow() instanceof JFrame) {
                ((JFrame) getWindow()).setContentPane(contentPane);
            }
        });
    }

    default void setDefaultCloseOperation(int operation) {
        SwingUtilities.invokeLater(() -> {
            if (getWindow() instanceof JDialog) {
                ((JDialog) getWindow()).setDefaultCloseOperation(operation);
            } else if (getWindow() instanceof JFrame) {
                ((JFrame) getWindow()).setDefaultCloseOperation(operation);
            }
        });
    }

    @RunOnDispatch
    default String getTitle() {
        if (getWindow() instanceof JDialog) {
            return ((JDialog) getWindow()).getTitle();
        } else if (getWindow() instanceof JFrame) {
            return ((JFrame) getWindow()).getTitle();
        }

        throw new IllegalStateException("Bug! Unimplemented window type.");
    }

    default void setTitle(String title) {
        SwingUtilities.invokeLater(() -> {
            if (getWindow() instanceof JDialog) {
                ((JDialog) getWindow()).setTitle(title);
            } else if (getWindow() instanceof JFrame) {
                ((JFrame) getWindow()).setTitle(title);
            }
        });
    }

    default void setAllowResizing(boolean resizable) {
        SwingUtilities.invokeLater(() -> {
            if (getWindow() instanceof JFrame) {
                ((JFrame) getWindow()).setResizable(resizable);
            }
            if (getWindow() instanceof JDialog) {
                ((JDialog) getWindow()).setResizable(resizable);
            }
        });
    }

    default void applyMenuBar() {
        SwingUtilities.invokeLater(() -> {
            if (getWindow() instanceof JFrame) {
                JFrame frame = (JFrame) getWindow();
                frame.setJMenuBar(ownsMenuBar() || WindowManager.getInstance().isMacOs() ? getWyldCardMenuBar() : null);
                frame.revalidate();
            }
        });
    }

    default void toggleVisible() {
        SwingUtilities.invokeLater(() -> getWindow().setVisible(!getWindow().isVisible()));
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
