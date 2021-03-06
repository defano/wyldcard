package com.defano.wyldcard.window.layout;

import com.defano.wyldcard.importer.ConversionProgressObserver;
import com.defano.wyldcard.thread.Invoke;
import com.defano.wyldcard.window.WyldCardDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class StackImportProgress extends WyldCardDialog implements ConversionProgressObserver {
    private JLabel progressTitle;
    private JProgressBar progressBar;
    private JPanel windowPanel;
    private JButton cancelButton;
    private JLabel progressLabel;

    private boolean cancelled = false;

    public StackImportProgress() {
        cancelButton.addActionListener(e -> {
            this.dispose();
            cancelled = true;
        });
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public JComponent getWindowPanel() {
        return windowPanel;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    @Override
    public JButton getDefaultButton() {
        return cancelButton;
    }

    public void setWaitingForStackToOpen() {
        Invoke.onDispatch(() -> {
            progressBar.setIndeterminate(true);
            progressBar.setValue(0);
            cancelButton.setEnabled(false);
            progressLabel.setText("");
            progressTitle.setText("Opening HyperCard stack...");
        });
    }

    @Override
    public void onConversionProgressUpdate(int cardsImported, int totalCards, String message) {
        Invoke.onDispatch(() -> {
            progressBar.setMinimum(1);
            progressBar.setMaximum(totalCards);
            progressBar.setValue(cardsImported);
            progressLabel.setText(message);
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        windowPanel = new JPanel();
        windowPanel.setLayout(new GridLayoutManager(5, 2, new Insets(10, 10, 10, 10), -1, -1));
        windowPanel.setMinimumSize(new Dimension(300, 150));
        progressTitle = new JLabel();
        progressTitle.setText("Importing HyperCard Stack...");
        windowPanel.add(progressTitle, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        progressBar = new JProgressBar();
        windowPanel.add(progressBar, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        windowPanel.add(spacer1, new GridConstraints(2, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        windowPanel.add(cancelButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        windowPanel.add(spacer2, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        progressLabel = new JLabel();
        progressLabel.setOpaque(false);
        progressLabel.setText("Reading stack file.");
        windowPanel.add(progressLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        windowPanel.add(spacer3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return windowPanel;
    }

}
