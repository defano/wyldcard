package hypercard.gui;

import hypercard.paint.model.Provider;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class HyperCardWindow extends WindowAdapter {

    // The Swing frame that this window is displayed in; bound only after the window has been built via WindowBuilder
    private JFrame windowFrame;
    private Provider<Boolean> windowVisibleProvider = new Provider<>(false);

    public abstract JPanel getWindowPanel();
    public abstract void bindModel(Object data);

    public JFrame getWindowFrame() {
        return windowFrame;
    }

    public void setWindowFrame(JFrame windowFrame) {
        this.windowFrame = windowFrame;
        this.windowFrame.addWindowListener(this);
    }

    public boolean isVisible() {
        return windowFrame != null && windowFrame.isVisible();
    }

    public void setVisible (boolean visible) {
        if (windowFrame != null) {
            windowFrame.setVisible(visible);
            windowVisibleProvider.set(visible);
        }
    }

    public void toggleVisible() {
        setVisible(!isVisible());
    }

    public void dispose() {
        SwingUtilities.getWindowAncestor(getWindowPanel()).dispose();
        windowVisibleProvider.set(false);
    }

    public Provider<Boolean> getWindowVisibleProvider() {
        return windowVisibleProvider;
    }

    public void windowClosed(WindowEvent e) {
        windowVisibleProvider.set(windowFrame.isVisible());
    }

}
