package hypercard.gui.window;

import hypercard.gui.HyperCardWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class WindowBuilder {

    private final JFrame frame;
    private final HyperCardWindow window;
    private Point location = null;
    private boolean initiallyVisible = true;

    private WindowBuilder (HyperCardWindow window) {
        this.window = window;
        this.frame = new JFrame();

        frame.setContentPane(window.getWindowPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
    }

    public static WindowBuilder make (HyperCardWindow window) {
        return new WindowBuilder(window);
    }

    public WindowBuilder withTitle (String title) {
        frame.setTitle(title);
        return this;
    }

    public WindowBuilder resizeable(boolean resizable) {
        frame.setResizable(resizable);
        return this;
    }

    public WindowBuilder quitOnClose() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return this;
    }

    public WindowBuilder withModel(Object model) {
        window.bindModel(model);
        return this;
    }

    public WindowBuilder withMenuBar (JMenuBar menuBar) {

        // Swing does not allow a JMenuBar to "live" on multiple windows at one; this lets us "steal" the
        // menubar each time the window comes into focus.
        frame.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                frame.setJMenuBar(menuBar);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });
        return this;
    }

    public WindowBuilder withLocationUnderneath (Component component) {
        frame.pack();

        int targetY = (int) component.getLocation().getY() + component.getHeight() + 10;
        int targetX = (int) component.getLocation().getX() - ((frame.getWidth() / 2) - (component.getWidth() / 2));
        location = new Point(targetX, targetY);

        return this;
    }

    public WindowBuilder withLocationRelativeTo(Component component) {
        frame.setLocationRelativeTo(component);
        return this;
    }

    public WindowBuilder notInitiallyVisible () {
        this.initiallyVisible = false;
        return this;
    }

    public JFrame build () {
        frame.pack();

        if (location == null) {
            frame.setLocationRelativeTo(null);
        } else {
            frame.setLocation(location);
        }

        frame.setVisible(initiallyVisible);

        return frame;
    }
}
