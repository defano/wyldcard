package com.defano.wyldcard.window.layouts;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.window.WyldCardDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class FontSizePicker extends WyldCardDialog<Object> {
    private JSpinner fontSize;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel windowPanel;

    public FontSizePicker() {
        fontSize.setModel(new SpinnerNumberModel(12, 2, Integer.MAX_VALUE, 1));

        okButton.addActionListener(e -> {
            WyldCard.getInstance().getFontManager().setSelectedFontSize((int) (fontSize.getValue()));
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }

    @Override
    public JComponent getWindowPanel() {
        return windowPanel;
    }

    @Override
    public void bindModel(Object data) {
        fontSize.setValue(WyldCard.getInstance().getFontManager().getSelectedFontSize().integerValue());
    }

    @Override
    public JButton getDefaultButton() {
        return okButton;
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
        windowPanel.setLayout(new GridLayoutManager(2, 5, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Font Size:");
        windowPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        windowPanel.add(spacer1, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        fontSize = new JSpinner();
        windowPanel.add(fontSize, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        okButton = new JButton();
        okButton.setText("OK");
        windowPanel.add(okButton, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        windowPanel.add(cancelButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        windowPanel.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return windowPanel;
    }
}
