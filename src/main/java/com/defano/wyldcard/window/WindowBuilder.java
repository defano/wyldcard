package com.defano.wyldcard.window;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowBuilder<ModelType, WindowType extends WyldCardFrame<?,ModelType>> {

    private final WindowType window;
    private boolean initiallyVisible = true;
    private boolean centeredOnScreen = false;
    private boolean resizable = false;
    private boolean isPalette = false;
    private boolean isFocusable = true;
    private WindowClosingAction actionOnClose = null;

    public WindowBuilder(WindowType window) {
        this.window = window;

        this.window.setContentPane(window.getWindowPanel());
        this.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Creates a JFrame intended to be used when creating card screenshots (for use in visual effects processing and
     * displaying card thumbnails).
     *
     * Swing has some seemingly odd requirements here. Components can only be printed if they're attached to a JFrame
     * and that frame has been made visible at some point. If these conditions are not met, calls to
     * {@link Component#printAll(Graphics)} produce empty or partially populated renderings. Ostensibly, this is a side
     * effect of Swings Java-to-native component peering architecture.
     *
     * @return A JFrame intended to be used for screen printing.
     */
    public static JFrame buildHiddenScreenshotFrame() {
        JFrame frame = new JFrame();

        // Frame doesn't have to be visible when grabbing screen, but *must* have been visible at some point.
        frame.setVisible(true);
        frame.setVisible(false);

        return frame;
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
    public WindowBuilder withModel(ModelType model) {
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

    public WindowBuilder withLocation(Point location) {
        if (location != null) {
            this.window.positionWindow(location.x, location.y);
        }
        return this;
    }

    @RunOnDispatch
    public WindowBuilder withLocationCenteredOnScreen() {
        this.centeredOnScreen = true;
        return this;
    }

    @RunOnDispatch
    public WindowBuilder withLocationCenteredOver(Component component) {
        this.window.setLocationCenteredOver(component);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder withLocationStaggeredOver(Component component) {
        this.window.getWindow().pack();
        this.window.setLocationStaggeredOver(component);
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

        this.window.setHasMenuBar(true);
        return this;
    }

    @RunOnDispatch
    public WindowBuilder asModal() {
        this.window.setIsModal();
        return this;
    }

    @RunOnDispatch
    public WindowType buildReplacing(WyldCardFrame window) {
        window.getWindow().dispose();
        return build();
    }

    @RunOnDispatch
    public WindowType build() {
        this.window.getWindow().pack();

        if (centeredOnScreen) {
            this.window.getWindow().setLocationRelativeTo(null);
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

        // Push palettes to back when WyldCard is not in foreground
        if (!isPalette) {
            this.window.getWindow().addWindowListener(new PaletteActivationManager());
        }

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
        this.window.getWindow().setVisible(initiallyVisible);

        // Very strange: When running inside IntelliJ on macOS, setResizable must be called after setVisible,
        // otherwise, the frame will "automagically" move to the lower left of the screen.
        // See: http://stackoverflow.com/questions/26332251/jframe-moves-to-the-bottom-left-corner-of-the-screen
        this.window.setAllowResizing(resizable);

        // Notify the DefaultWindowManager when a new window is opened or closed
        this.window.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                WyldCard.getInstance().getWindowManager().notifyWindowVisibilityChanged();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                WyldCard.getInstance().getWindowManager().notifyWindowVisibilityChanged();
            }
        });

        return window;
    }

}
