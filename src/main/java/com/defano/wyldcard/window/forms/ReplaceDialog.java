package com.defano.wyldcard.window.forms;

import com.defano.wyldcard.window.HyperCardDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class ReplaceDialog extends HyperCardDialog {
    private JTextField findField;
    private JRadioButton wholeWordRadioButton;
    private JRadioButton partialWordRadioButton;
    private JCheckBox caseSensitiveCheckBox;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private JTextField replaceField;
    private JPanel replacePanel;
    private JCheckBox wraparoundCheckBox;
    private JButton findButton;
    private JButton cancelButton;

    private ScriptEditor editor;

    public ReplaceDialog() {
        cancelButton.addActionListener(e -> this.setVisible(false));

        replaceButton.addActionListener(e ->
                editor.replace(
                        findField.getText(),
                        replaceField.getText(),
                        wholeWordRadioButton.isSelected(),
                        caseSensitiveCheckBox.isSelected(),
                        wraparoundCheckBox.isSelected())
        );

        replaceAllButton.addActionListener(e ->
                editor.replaceAll(
                        findField.getText(),
                        replaceField.getText(),
                        wholeWordRadioButton.isSelected(),
                        caseSensitiveCheckBox.isSelected()));

        findButton.addActionListener(e ->
                editor.find(
                        findField.getText(),
                        wholeWordRadioButton.isSelected(),
                        caseSensitiveCheckBox.isSelected(),
                        wraparoundCheckBox.isSelected()));
    }

    @Override
    public JComponent getWindowPanel() {
        return replacePanel;
    }

    @Override
    public void bindModel(Object data) {
        editor = (ScriptEditor) data;

        wholeWordRadioButton.setSelected(editor.getContext().getWholeWord());
        caseSensitiveCheckBox.setSelected(editor.getContext().getMatchCase());
        findField.setText(editor.getContext().getSearchFor());
        replaceField.setText(editor.getContext().getReplaceWith());
    }

    @Override
    public JButton getDefaultButton() {
        return replaceButton;
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
        replacePanel = new JPanel();
        replacePanel.setLayout(new GridLayoutManager(7, 2, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Find:");
        replacePanel.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        findField = new JTextField();
        replacePanel.add(findField, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        wholeWordRadioButton = new JRadioButton();
        wholeWordRadioButton.setText("Whole Word");
        replacePanel.add(wholeWordRadioButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partialWordRadioButton = new JRadioButton();
        partialWordRadioButton.setSelected(true);
        partialWordRadioButton.setText("Partial Word");
        replacePanel.add(partialWordRadioButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        caseSensitiveCheckBox = new JCheckBox();
        caseSensitiveCheckBox.setText("Case Sensitive");
        replacePanel.add(caseSensitiveCheckBox, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        wraparoundCheckBox = new JCheckBox();
        wraparoundCheckBox.setSelected(true);
        wraparoundCheckBox.setText("Wraparound Search");
        replacePanel.add(wraparoundCheckBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 5, new Insets(15, 0, 0, 0), -1, -1));
        replacePanel.add(panel1, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        replaceButton = new JButton();
        replaceButton.setText("Replace");
        panel1.add(replaceButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        replaceAllButton = new JButton();
        replaceAllButton.setText("Replace All");
        panel1.add(replaceAllButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        findButton = new JButton();
        findButton.setText("Find");
        panel1.add(findButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        panel1.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Replace With:");
        replacePanel.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        replaceField = new JTextField();
        replacePanel.add(replaceField, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(partialWordRadioButton);
        buttonGroup.add(wholeWordRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return replacePanel;
    }
}
