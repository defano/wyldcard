package com.defano.wyldcard.window;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowBuilder<T extends WyldCardFrame> {

    private final static int DEFAULT_SEPARATION = 10;

    private final T window;
    private Point location = null;
    private Component relativeLocation = null;
    private boolean initiallyVisible = true;
    private boolean resizable = false;
    private WyldCardWindow dock;
    private boolean isPalette = false;
    private boolean isFocusable = true;
    private WindowClosingAction actionOnClose = null;

    private WindowBuilder(T window) {
        this.window = window;

        this.window.setContentPane(window.getWindowPanel());
        this.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static WindowBuilder<WyldCardFrame> make(WyldCardWindow window) {
        return new WindowBuilder<>(window);
    }

    public static WindowBuilder<WyldCardDialog> make(WyldCardDialog window) {
        return new WindowBuilder<>(window);
    }

    @RunOnDispatch
    public WindowBuilder withTitle(String title) {
        this.window.setTitle(title);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder resizeable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    @RunOnDispatch
    public WindowBuilder setDefaultCloseOperation(int operation) {
        window.setDefaultCloseOperation(operation);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder withActionOnClose(WindowClosingAction actionOnClose) {
        this.actionOnClose = actionOnClose;
        return this;
    }

    @RunOnDispatch
    public WindowBuilder withModel(Object model) {
        window.bindModel(model);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder asPalette() {
        this.isPalette = true;
        this.isFocusable = false;
        this.window.getWindow().setType(Window.Type.UTILITY);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder focusable(boolean focusable) {
        this.isFocusable = focusable;
        return this;
    }

    @RunOnDispatch
    public WindowBuilder dockTo(WyldCardWindow window) {
        this.dock = window;
        return this;
    }

    public WindowBuilder withLocation(int x, int y) {
        location = new Point(x, y);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder withLocationUnderneath(Component component) {
        this.window.getWindow().pack();

        int targetY = (int) component.getLocation().getY() + component.getHeight() + DEFAULT_SEPARATION;
        int targetX = (int) component.getLocation().getX() - ((window.getWindow().getWidth() / 2) - (component.getWidth() / 2));
        location = new Point(targetX, targetY);

        return this;
    }

    @RunOnDispatch
    public WindowBuilder withLocationLeftOf(Component component) {
        this.window.getWindow().pack();

        int targetY = (int) component.getLocation().getY();
        int targetX = (int) component.getLocation().getX() - window.getWindow().getWidth() - DEFAULT_SEPARATION;
        location = new Point(targetX, targetY);

        return this;
    }

    @RunOnDispatch
    public WindowBuilder withLocationCenteredOver(Component component) {
        relativeLocation = component;
        return this;
    }

    @RunOnDispatch
    public WindowBuilder withLocationStaggeredOver(Component component) {
        this.window.getWindow().pack();
        location = new Point((int) component.getLocationOnScreen().getX() + DEFAULT_SEPARATION, (int) component.getLocationOnScreen().getY() + DEFAULT_SEPARATION);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder notInitiallyVisible() {
        this.initiallyVisible = false;
        return this;
    }

    @RunOnDispatch
    public WindowBuilder ownsMenubar() {
        if (window instanceof WyldCardDialog) {
            throw new IllegalStateException("This type of window cannot own the menubar.");
        }

        this.window.setOwnsMenuBar(true);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder asModal() {
        this.window.setIsModal();
        return this;
    }

    @RunOnDispatch
    public T buildReplacing(WyldCardFrame window) {
        window.getWindow().dispose();
        return build();
    }

    @RunOnDispatch
    public T build() {
        this.window.getWindow().pack();

        if (location != null) {
            this.window.positionWindow(location.x, location.y);
        } else {
            this.window.getWindow().setLocationRelativeTo(relativeLocation);
        }

        if (window instanceof WyldCardDialog) {
            this.window.setAllowResizing(resizable);
        }

        if (window.getDefaultButton() != null) {
            SwingUtilities.getRootPane(window.getDefaultButton()).setDefaultButton(window.getDefaultButton());
        }

        if (actionOnClose != null) {
            this.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            this.window.getWindow().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    actionOnClose.onWindowClosing((WyldCardFrame) e.getWindow());
                }
            });
        }

        // Notify the WindowManager when a new window is opened or closed
        this.window.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                WindowManager.getInstance().notifyWindowVisibilityChanged();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                WindowManager.getInstance().notifyWindowVisibilityChanged();
            }
        });

        // Very strange: When running inside IntelliJ on macOS, setResizable must be called after setVisible,
        // otherwise, the frame will "automagically" move to the lower left of the screen.
        // See: http://stackoverflow.com/questions/26332251/jframe-moves-to-the-bottom-left-corner-of-the-screen

        this.window.setAllowResizing(resizable);
        this.window.getWindow().setFocusableWindowState(isFocusable);
        this.window.getWindow().setAlwaysOnTop(isPalette);

        // Calculate and set minimum allowable frame size
        int minWidth = window.getWindowPanel().getMinimumSize().width;
        int minHeight = window.getWindowPanel().getMinimumSize().height;
        if (window.getWindowPanel().getLayout() instanceof GridLayoutManager) {
            GridLayoutManager glm = (GridLayoutManager) window.getWindowPanel().getLayout();
            minWidth += glm.getMargin().left + glm.getMargin().right;
            minHeight += glm.getMargin().top + glm.getMargin().bottom;
        }
        this.window.getWindow().setMinimumSize(new Dimension(minWidth, minHeight));

        if (dock != null) {

            if (isPalette) {
                dock.getWindow().addWindowListener(new PaletteActivationManager(window));
            }

            // When dock window moves, move docked windows too (keep relative window layout)
            dock.getWindow().addComponentListener(new ComponentAdapter() {
                private Point lastLocation;

                @Override
                public void componentMoved(ComponentEvent e) {
                    super.componentMoved(e);
                    Point location = e.getComponent().getLocation();

                    if (lastLocation != null) {
                        int deltaX = location.x - lastLocation.x;
                        int deltaY = location.y - lastLocation.y;

                        window.getWindow().setLocation(window.getWindow().getLocation().x + deltaX, window.getWindow().getLocation().y + deltaY);
                    }

                    lastLocation = location;
                }
            });
        }

        this.window.applyMenuBar();
        this.window.getWindow().setVisible(initiallyVisible);

        return window;
    }

}
