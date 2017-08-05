/*
 * ButtonPropertyEditor
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.window;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.gui.HyperCardDialog;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.defano.hypercard.parts.button.ButtonStyle;
import com.defano.hypercard.parts.button.ButtonModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.l2fprod.common.swing.JFontChooser;

import javax.swing.*;
import java.awt.*;

public class ButtonPropertyEditor extends HyperCardDialog {
    private PartModel model;

    private JButton saveButton;
    private JButton cancelButton;
    private JButton editScriptButton;
    private JTextField buttonName;
    private JTextField buttonHeight;
    private JTextField buttonWidth;
    private JTextField buttonTop;
    private JTextField buttonLeft;
    private JCheckBox isVisible;
    private JCheckBox isShowTitle;
    private JCheckBox isEnabled;
    private JPanel buttonEditor;
    private JComboBox style;
    private JComboBox family;
    private JButton contents;
    private JCheckBox autoHilite;
    private JPanel coordinatePanel;
    private JLabel buttonLabel;
    private JLabel partLabel;
    private JLabel buttonLabelValue;
    private JLabel partLabelValue;
    private JLabel idLabelValue;
    private JButton textStyle;

    @SuppressWarnings("unchecked")
    public ButtonPropertyEditor() {
        editScriptButton.addActionListener(e -> {
            dispose();
            WindowBuilder.make(new ScriptEditor())
                    .withTitle("Script of button " + model.getKnownProperty(ButtonModel.PROP_NAME).stringValue())
                    .withModel(model)
                    .resizeable(true)
                    .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                    .build();
        });

        cancelButton.addActionListener(e -> dispose());
        contents.addActionListener(e -> showContentsEditor());

        saveButton.addActionListener(e -> {
            updateProperties();
            dispose();
        });

        textStyle.addActionListener(e -> ((CardLayerPartModel) model).setFont(JFontChooser.showDialog(getWindowPanel(), "Choose Font", ((CardLayerPartModel) model).getFont())));

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (ButtonStyle thisStyle : ButtonStyle.values()) {
            model.addElement(thisStyle.getName());
        }
        style.setModel(model);
    }

    @Override
    public JPanel getWindowPanel() {
        return buttonEditor;
    }

    @Override
    public void bindModel(Object data) {
        this.model = (PartModel) data;

        PartModel part = HyperCard.getInstance().getCard().findPartOnCard(model.getType(), model.getKnownProperty(PartModel.PROP_ID).integerValue());
        long partNumber = HyperCard.getInstance().getCard().getPartNumber(part);
        long buttonNumber = HyperCard.getInstance().getCard().getButtonNumber((ButtonModel) part);
        long buttonCount = HyperCard.getInstance().getCard().getPartCount(model.getType(), part.getOwner());
        long partCount = HyperCard.getInstance().getCard().getPartCount(null, part.getOwner());
        String layer = part.getOwner().friendlyName;

        buttonLabel.setText(layer + " Button:");
        buttonLabelValue.setText(buttonNumber + " of " + buttonCount);

        partLabel.setText(layer + " Part:");
        partLabelValue.setText(partNumber + " of " + partCount);
        idLabelValue.setText(String.valueOf(part.getId()));

        buttonName.setText(model.getKnownProperty(ButtonModel.PROP_NAME).stringValue());
        buttonTop.setText(model.getKnownProperty(ButtonModel.PROP_TOP).stringValue());
        buttonLeft.setText(model.getKnownProperty(ButtonModel.PROP_LEFT).stringValue());
        buttonHeight.setText(model.getKnownProperty(ButtonModel.PROP_HEIGHT).stringValue());
        buttonWidth.setText(model.getKnownProperty(ButtonModel.PROP_WIDTH).stringValue());
        isEnabled.setSelected(model.getKnownProperty(ButtonModel.PROP_ENABLED).booleanValue());
        isShowTitle.setSelected(model.getKnownProperty(ButtonModel.PROP_SHOWNAME).booleanValue());
        isVisible.setSelected(model.getKnownProperty(ButtonModel.PROP_VISIBLE).booleanValue());
        style.setSelectedItem(model.getKnownProperty(ButtonModel.PROP_STYLE).stringValue());
        family.setSelectedItem(model.getKnownProperty(ButtonModel.PROP_FAMILY).stringValue());
        autoHilite.setSelected(model.getKnownProperty(ButtonModel.PROP_AUTOHILIGHT).booleanValue());
    }

    private void updateProperties() {
        model.setKnownProperty(ButtonModel.PROP_NAME, new Value(buttonName.getText()));
        model.setKnownProperty(ButtonModel.PROP_TOP, new Value(buttonTop.getText()));
        model.setKnownProperty(ButtonModel.PROP_LEFT, new Value(buttonLeft.getText()));
        model.setKnownProperty(ButtonModel.PROP_HEIGHT, new Value(buttonHeight.getText()));
        model.setKnownProperty(ButtonModel.PROP_WIDTH, new Value(buttonWidth.getText()));
        model.setKnownProperty(ButtonModel.PROP_ENABLED, new Value(isEnabled.isSelected()));
        model.setKnownProperty(ButtonModel.PROP_SHOWNAME, new Value(isShowTitle.isSelected()));
        model.setKnownProperty(ButtonModel.PROP_VISIBLE, new Value(isVisible.isSelected()));
        model.setKnownProperty(ButtonModel.PROP_STYLE, new Value(style.getSelectedItem().toString()));
        model.setKnownProperty(ButtonModel.PROP_FAMILY, new Value(family.getSelectedItem().toString()));
        model.setKnownProperty(ButtonModel.PROP_AUTOHILIGHT, new Value(autoHilite.isSelected()));
    }

    private void showContentsEditor() {
        String contents = PartContentsEditor.editContents(model.getKnownProperty(PartModel.PROP_CONTENTS).stringValue(), getWindowPanel());
        if (contents != null) {
            model.setKnownProperty(PartModel.PROP_CONTENTS, new Value(contents));
        }
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
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
        buttonEditor = new JPanel();
        buttonEditor.setLayout(new GridLayoutManager(4, 5, new Insets(10, 10, 10, 10), -1, -1));
        buttonEditor.setMaximumSize(new Dimension(587, 257));
        coordinatePanel = new JPanel();
        coordinatePanel.setLayout(new GridLayoutManager(3, 6, new Insets(5, 5, 5, 5), -1, -1));
        buttonEditor.add(coordinatePanel, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coordinatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Identification"));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(4);
        label1.setText("Name:");
        coordinatePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(90, -1), null, new Dimension(90, -1), 0, false));
        buttonName = new JTextField();
        coordinatePanel.add(buttonName, new GridConstraints(0, 1, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonLabel = new JLabel();
        buttonLabel.setText("Card Button:");
        coordinatePanel.add(buttonLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonLabelValue = new JLabel();
        buttonLabelValue.setText("Label");
        coordinatePanel.add(buttonLabelValue, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partLabel = new JLabel();
        partLabel.setText("Card Part:");
        coordinatePanel.add(partLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partLabelValue = new JLabel();
        partLabelValue.setText("Label");
        coordinatePanel.add(partLabelValue, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        coordinatePanel.add(spacer1, new GridConstraints(1, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        idLabelValue = new JLabel();
        idLabelValue.setText("Label");
        coordinatePanel.add(idLabelValue, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Button ID:");
        coordinatePanel.add(label2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 5, new Insets(5, 5, 5, 5), -1, -1));
        buttonEditor.add(panel1, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Location"));
        final JLabel label3 = new JLabel();
        label3.setText("Width:");
        panel1.add(label3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonHeight = new JTextField();
        panel1.add(buttonHeight, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        buttonWidth = new JTextField();
        panel1.add(buttonWidth, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Top:");
        panel1.add(label4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Left:");
        panel1.add(label5, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonTop = new JTextField();
        panel1.add(buttonTop, new GridConstraints(0, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        buttonLeft = new JTextField();
        panel1.add(buttonLeft, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Height:");
        panel1.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 4, new Insets(5, 5, 5, 5), -1, -1));
        buttonEditor.add(panel2, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Look and Feel"));
        style = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        style.setModel(defaultComboBoxModel1);
        panel2.add(style, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        family = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("None");
        defaultComboBoxModel2.addElement("1");
        defaultComboBoxModel2.addElement("2");
        defaultComboBoxModel2.addElement("3");
        defaultComboBoxModel2.addElement("4");
        defaultComboBoxModel2.addElement("5");
        defaultComboBoxModel2.addElement("6");
        defaultComboBoxModel2.addElement("7");
        defaultComboBoxModel2.addElement("8");
        defaultComboBoxModel2.addElement("9");
        defaultComboBoxModel2.addElement("10");
        defaultComboBoxModel2.addElement("11");
        defaultComboBoxModel2.addElement("12");
        defaultComboBoxModel2.addElement("13");
        defaultComboBoxModel2.addElement("14");
        defaultComboBoxModel2.addElement("15");
        family.setModel(defaultComboBoxModel2);
        panel2.add(family, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Style:");
        panel2.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Family:");
        panel2.add(label8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoHilite = new JCheckBox();
        autoHilite.setMargin(new Insets(0, 1, 0, 1));
        autoHilite.setText("Auto Hilite");
        autoHilite.setToolTipText("When selected the system will automatically hilite the button when the mouse is pressed over it. Deselect to control this behavior in script.");
        panel2.add(autoHilite, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isVisible = new JCheckBox();
        isVisible.setMargin(new Insets(0, 1, 0, 1));
        isVisible.setText("Visible");
        isVisible.setToolTipText("When selected the button will be visible on the card (note that buttons are always visible when the button tool is selected).");
        panel2.add(isVisible, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isShowTitle = new JCheckBox();
        isShowTitle.setMargin(new Insets(0, 1, 0, 1));
        isShowTitle.setText("Show Name");
        isShowTitle.setToolTipText("When selected the button's name will be drawn on the button.");
        panel2.add(isShowTitle, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel2.add(spacer3, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        isEnabled = new JCheckBox();
        isEnabled.setMargin(new Insets(0, 1, 0, 1));
        isEnabled.setText("Enabled");
        isEnabled.setToolTipText("When selected, button will appear enabled (not \"greyed out\"). Note that buttons continue to receive HyperTalk messages even when they're disabled.");
        panel2.add(isEnabled, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        buttonEditor.add(spacer4, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        editScriptButton = new JButton();
        editScriptButton.setText("Edit Script...");
        editScriptButton.setToolTipText("");
        buttonEditor.add(editScriptButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textStyle = new JButton();
        textStyle.setText("Text Style...");
        buttonEditor.add(textStyle, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contents = new JButton();
        contents.setText("Contents...");
        buttonEditor.add(contents, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        buttonEditor.add(cancelButton, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        buttonEditor.add(saveButton, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return buttonEditor;
    }
}
