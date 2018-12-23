package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.menubar.main.HyperCardMenuBar;
import com.defano.wyldcard.util.ThreadUtils;
import io.reactivex.Observable;

import javax.swing.*;
import java.awt.*;

public interface WyldCardFrame<WindowType extends Window, ModelType> {

    int DEFAULT_SEPARATION = 10;

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
    void bindModel(ModelType data);

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

    /**
     * Determines if this window has a menu bar.
     *
     * @return True if the window has a menu bar, false otherwise.
     */
    boolean hasMenuBar();

    /**
     * Specifies if this window has a menu bar. On Mac OS X systems, windows without a menu bar inherit the system menu
     * bar, {@link HyperCardMenuBar}.
     *
     * @param ownsMenuBar True if this window should have a menu bar
     */
    void setHasMenuBar(boolean ownsMenuBar);

    /**
     * Gets the JMenuBar that should be applied to this window; by default, returns the {@link HyperCardMenuBar}.
     *
     * @return The menu bar belonging to this window.
     */
    default JMenuBar getWyldCardMenuBar() {
        return HyperCardMenuBar.getInstance();
    }

    /**
     * Gets an observable indication of whether this window is presently visible.
     *
     * @return An observable of whether this window is visible
     */
    Observable<Boolean> getWindowVisibleProvider();

    /**
     * Gets an observerable indication of whether this window has focus.
     *
     * @return An observable of whether this window is focused
     */
    Observable<Boolean> getWindowFocusedProvider();

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

    /**
     * Sets the location of this window, but adjusting the actual location of the window as necessary to assure that no
     * portion of the window appears offscreen.
     *
     * @param x The requested x coordinate of the window
     * @param y The requested y coordinate of the window
     */
    @RunOnDispatch
    default void positionWindow(int x, int y) {
        DisplayMode mode = getWindow().getGraphicsConfiguration().getDevice().getDisplayMode();

        int xPos = Math.min(x, mode.getWidth() - getWindow().getWidth());
        int yPos = Math.min(y, mode.getHeight() - getWindow().getHeight());
        xPos = Math.max(0, xPos);
        yPos = Math.max(0, yPos);

        getWindow().setLocation(xPos, yPos);
    }

    default boolean isPalette() {
        return ThreadUtils.callAndWaitAsNeeded(() -> getWindow().getType() == Window.Type.UTILITY);
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
                if (hasMenuBar() || WyldCard.getInstance().getWindowManager().isMacOsTheme()) {
                    frame.setJMenuBar(getWyldCardMenuBar());
                    frame.revalidate();
                }

                // Clear previously applied menubar (applicable to some systems when changing themes)
                else {
                    frame.setJMenuBar(null);
                    frame.revalidate();
                }
            }
        });
    }

    default void toggleVisible() {
        SwingUtilities.invokeLater(() -> {
            getWindow().setVisible(!getWindow().isVisible());
        });
    }

    @RunOnDispatch
    default void setIsModal() {
        if (getWindow() instanceof JDialog) {
            ((JDialog) getWindow()).setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        } else {
            throw new IllegalStateException("This kind of window cannot be made modal");
        }
    }

    @RunOnDispatch
    default WyldCardFrame<WindowType, ModelType> setLocationRightOf(Component component) {
        int targetX = component.getX() + component.getWidth() + DEFAULT_SEPARATION;
        getWindow().setLocation(targetX, getWindow().getY());
        return this;
    }

    @RunOnDispatch
    default WyldCardFrame<WindowType, ModelType> setLocationLeftOf(Component component) {
        int targetX = component.getX() - getWindow().getWidth() - DEFAULT_SEPARATION;
        getWindow().setLocation(targetX, getWindow().getY());
        return this;
    }

    @RunOnDispatch
    default WyldCardFrame<WindowType, ModelType> setLocationBelow(Component component) {
        int targetY = component.getY() + component.getHeight() + DEFAULT_SEPARATION;
        getWindow().setLocation(getWindow().getX(), targetY);
        return this;
    }

    @RunOnDispatch
    default WyldCardFrame<WindowType, ModelType> alignTopTo(Component component) {
        getWindow().setLocation(getWindow().getX(), component.getY());
        return this;
    }

    @RunOnDispatch
    default WyldCardFrame<WindowType, ModelType> alignLeftTo(Component component) {
        getWindow().setLocation(component.getX(), getWindow().getY());
        return this;
    }

    @RunOnDispatch
    default WyldCardFrame<WindowType, ModelType> alignTopStaggeredTo(Component component) {
        getWindow().setLocation(getWindow().getX(), component.getY() + DEFAULT_SEPARATION);
        return this;
    }

    @RunOnDispatch
    default void setLocationCenteredOver(Component component) {
        getWindow().setLocationRelativeTo(component);
    }

    @RunOnDispatch
    default void setLocationStaggeredOver(Component component) {
        getWindow().setLocation(new Point(
                (int) component.getLocationOnScreen().getX() + DEFAULT_SEPARATION,
                (int) component.getLocationOnScreen().getY() + DEFAULT_SEPARATION)
        );
    }
}