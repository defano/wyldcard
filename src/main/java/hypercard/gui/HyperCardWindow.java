package hypercard.gui;

import javax.swing.*;

public abstract class HyperCardWindow {

    // The Swing frame that this window is displayed in; bound only after the window has been built via WindowBuilder
    private JFrame windowFrame;

    public abstract JPanel getWindowPanel();
    public abstract void bindModel(Object data);

    public JFrame getWindowFrame() {
        return windowFrame;
    }

    public void setWindowFrame(JFrame windowFrame) {
        this.windowFrame = windowFrame;
    }

    public boolean isVisible() {
        return windowFrame != null && windowFrame.isVisible();
    }

    public void setVisible (boolean visible) {
        if (windowFrame != null) {
            windowFrame.setVisible(visible);
        }
    }

    public void close () {
        SwingUtilities.getWindowAncestor(getWindowPanel()).dispose();
    }
}
