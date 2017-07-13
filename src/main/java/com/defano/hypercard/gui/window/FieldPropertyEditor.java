/*
 * FieldPropertyEditor
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.window;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.ButtonPart;
import com.defano.hypercard.parts.FieldPart;
import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.fields.FieldStyle;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.model.FieldModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.defano.hypercard.gui.HyperCardWindow;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;

public class FieldPropertyEditor extends HyperCardWindow {

    private PartModel model;

    private JPanel fieldEditor;
    private JTextField fieldName;
    private JLabel idLabelValue;
    private JTextField fieldHeight;
    private JTextField fieldWidth;
    private JTextField fieldTop;
    private JTextField fieldLeft;
    private JCheckBox isVisible;
    private JCheckBox isWrapText;
    private JCheckBox isLockText;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton editScriptButton;
    private JComboBox style;
    private JCheckBox showLines;
    private JLabel fieldLabel;
    private JLabel partLabel;
    private JLabel partLabelValue;
    private JLabel fieldLabelValue;

    public FieldPropertyEditor() {
        editScriptButton.addActionListener(e -> {
            dispose();
            WindowBuilder.make(new ScriptEditor())
                    .withTitle("Script for field " + fieldName.getText())
                    .withModel(model)
                    .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                    .resizeable(true)
                    .build();
        });
        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> {
            updateProperties();
            dispose();
        });

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (FieldStyle thisStyle : FieldStyle.values()) {
            model.addElement(thisStyle.getName());
        }
        style.setModel(model);

    }

    @Override
    public JPanel getWindowPanel() {
        return fieldEditor;
    }

    @Override
    public void bindModel(Object data) {
        if (data instanceof PartModel) {
            this.model = (PartModel) data;

            Part part = HyperCard.getInstance().getCard().findPartOnCard(model.getType(), model.getKnownProperty(PartModel.PROP_ID).integerValue());
            long partNumber = HyperCard.getInstance().getCard().getPartNumber(part);
            long fieldNumber = HyperCard.getInstance().getCard().getFieldNumber((FieldPart) part);
            long fieldCount = HyperCard.getInstance().getCard().getPartCount(model.getType(), part.getCardLayer());
            long partCount = HyperCard.getInstance().getCard().getPartCount(null, part.getCardLayer());
            String layer = part.getCardLayer().friendlyName;

            fieldLabel.setText(layer + " Field:");
            fieldLabelValue.setText(fieldNumber + " of " + fieldCount);

            partLabel.setText(layer + " Part:");
            partLabelValue.setText(partNumber + " of " + partCount);
            idLabelValue.setText(String.valueOf(part.getId()));

            fieldName.setText(model.getKnownProperty(FieldModel.PROP_NAME).stringValue());
            idLabelValue.setText(model.getKnownProperty(FieldModel.PROP_ID).stringValue());
            fieldTop.setText(model.getKnownProperty(FieldModel.PROP_TOP).stringValue());
            fieldLeft.setText(model.getKnownProperty(FieldModel.PROP_LEFT).stringValue());
            fieldHeight.setText(model.getKnownProperty(FieldModel.PROP_HEIGHT).stringValue());
            fieldWidth.setText(model.getKnownProperty(FieldModel.PROP_WIDTH).stringValue());
            isLockText.setSelected(model.getKnownProperty(FieldModel.PROP_LOCKTEXT).booleanValue());
            isVisible.setSelected(model.getKnownProperty(FieldModel.PROP_VISIBLE).booleanValue());
            isWrapText.setSelected(model.getKnownProperty(FieldModel.PROP_DONTWRAP).booleanValue());
            showLines.setSelected(model.getKnownProperty(FieldModel.PROP_SHOWLINES).booleanValue());
            style.setSelectedItem(model.getKnownProperty(FieldModel.PROP_STYLE).stringValue());
        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window: " + model);
        }
    }

    private void updateProperties() {
        model.setKnownProperty(FieldModel.PROP_NAME, new Value(fieldName.getText()));
        model.setKnownProperty(FieldModel.PROP_TOP, new Value(fieldTop.getText()));
        model.setKnownProperty(FieldModel.PROP_LEFT, new Value(fieldLeft.getText()));
        model.setKnownProperty(FieldModel.PROP_HEIGHT, new Value(fieldHeight.getText()));
        model.setKnownProperty(FieldModel.PROP_WIDTH, new Value(fieldWidth.getText()));
        model.setKnownProperty(FieldModel.PROP_LOCKTEXT, new Value(isLockText.isSelected()));
        model.setKnownProperty(FieldModel.PROP_VISIBLE, new Value(isVisible.isSelected()));
        model.setKnownProperty(FieldModel.PROP_DONTWRAP, new Value(isWrapText.isSelected()));
        model.setKnownProperty(FieldModel.PROP_SHOWLINES, new Value(showLines.isSelected()));
        model.setKnownProperty(FieldModel.PROP_STYLE, new Value(style.getSelectedItem().toString()));
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        fieldEditor = new JPanel();
        fieldEditor.setLayout(new GridLayoutManager(3, 3, new Insets(10, 10, 10, 10), -1, -1));
        fieldEditor.setMaximumSize(new Dimension(587, 257));
        panel1.add(fieldEditor, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 5, new Insets(5, 5, 5, 5), -1, -1));
        fieldEditor.add(panel2, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Identification"));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(4);
        label1.setText("Field Name:");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(90, -1), null, new Dimension(90, -1), 0, false));
        fieldLabel = new JLabel();
        fieldLabel.setText("Card Field:");
        panel2.add(fieldLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partLabel = new JLabel();
        partLabel.setText("Card Part:");
        panel2.add(partLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partLabelValue = new JLabel();
        partLabelValue.setText("Label");
        panel2.add(partLabelValue, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldLabelValue = new JLabel();
        fieldLabelValue.setText("Label");
        panel2.add(fieldLabelValue, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(2);
        label2.setInheritsPopupMenu(false);
        label2.setText("Field ID:");
        panel2.add(label2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idLabelValue = new JLabel();
        idLabelValue.setText("Label");
        panel2.add(idLabelValue, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldName = new JTextField();
        panel2.add(fieldName, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 4, new Insets(5, 5, 5, 5), -1, -1));
        fieldEditor.add(panel3, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Location"));
        final JLabel label3 = new JLabel();
        label3.setText("Height:");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Width:");
        panel3.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldHeight = new JTextField();
        panel3.add(fieldHeight, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        fieldWidth = new JTextField();
        panel3.add(fieldWidth, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Top:");
        panel3.add(label5, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Left:");
        panel3.add(label6, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldTop = new JTextField();
        panel3.add(fieldTop, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        fieldLeft = new JTextField();
        panel3.add(fieldLeft, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        fieldEditor.add(panel4, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel4.add(saveButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        panel4.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel4.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 3, new Insets(5, 5, 5, 5), -1, -1));
        fieldEditor.add(panel5, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Look and Feel"));
        isWrapText = new JCheckBox();
        isWrapText.setText("Don't Wrap");
        panel5.add(isWrapText, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isLockText = new JCheckBox();
        isLockText.setText("Lock Text");
        panel5.add(isLockText, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        style = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        style.setModel(defaultComboBoxModel1);
        panel5.add(style, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Style:");
        panel5.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isVisible = new JCheckBox();
        isVisible.setText("Visible");
        panel5.add(isVisible, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showLines = new JCheckBox();
        showLines.setText("Show Lines");
        panel5.add(showLines, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editScriptButton = new JButton();
        editScriptButton.setText("Edit Script...");
        fieldEditor.add(editScriptButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        fieldEditor.add(spacer4, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }
}
