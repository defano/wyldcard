package hypercard.gui.window;

import hypercard.gui.HyperCardWindow;
import hypercard.gui.menu.MenuItemBuilder;
import hypercard.runtime.RuntimeEnv;

import javax.swing.*;
import java.awt.*;

public class WindowBuilder {

    private final JFrame frame;
    private final HyperCardWindow window;

    private WindowBuilder (HyperCardWindow window) {
        this.window = window;
        this.frame = new JFrame();

        frame.setContentPane(window.getWindowPanel());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
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
        frame.setJMenuBar(menuBar);
        return this;
    }

    public WindowBuilder withLocationRelativeTo(Component component) {
        frame.setLocationRelativeTo(component);
        return this;
    }

    public void build () {
        frame.pack();
        frame.setVisible(true);
    }
}
