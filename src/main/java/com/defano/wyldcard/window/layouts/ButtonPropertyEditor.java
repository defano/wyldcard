package com.defano.wyldcard.window.layouts;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.fonts.TextStyleSpecifier;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.button.ButtonStyle;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.ActionBindable;
import com.defano.wyldcard.window.WyldCardDialog;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.hypertalk.ast.model.Value;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.l2fprod.common.swing.JFontChooser;

import javax.swing.*;
import java.awt.*;

public class ButtonPropertyEditor extends WyldCardDialog<ButtonModel> implements ActionBindable {
    private ButtonModel model;

    private JButton saveButton;
    private JButton editScriptButton;
    private JTextField buttonName;
    private JSpinner buttonHeight;
    private JSpinner buttonWidth;
    private JSpinner buttonTop;
    private JSpinner buttonLeft;
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
    private JButton iconButton;
    private JCheckBox sharedHilite;

    @SuppressWarnings("unchecked")
    public ButtonPropertyEditor() {
        editScriptButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> model.editScript(new ExecutionContext()));
        });

        contents.addActionListener(e -> showContentsEditor());

        saveButton.addActionListener(e -> {
            updateProperties();
            dispose();
        });

        iconButton.addActionListener(e -> new WindowBuilder<>(new IconPicker())
                .withModel(model)
                .withTitle("Icon")
                .resizeable(false)
                .asModal()
                .buildReplacing(this));

        textStyle.addActionListener(e -> {
            dispose();
            Font selection = JFontChooser.showDialog(getWindowPanel(), "Choose Font", model.getTextStyle(new ExecutionContext()).toFont());
            if (selection != null) {
                model.setTextStyle(new ExecutionContext(), TextStyleSpecifier.fromFont(selection));
            }
        });

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (ButtonStyle thisStyle : ButtonStyle.values()) {
            model.addElement(thisStyle);
        }
        style.setModel(model);

    }

    @Override
    public JButton getDefaultButton() {
        return saveButton;
    }

    @Override
    public JPanel getWindowPanel() {
        return buttonEditor;
    }

    @Override
    @RunOnDispatch
    public void bindModel(ButtonModel buttonModel) {
        this.model = buttonModel;
        ExecutionContext context = new ExecutionContext();

        long partNumber = model.getPartNumber(context);
        long buttonNumber = model.getButtonOrFieldNumber(context);
        long buttonCount = model.getButtonOrFieldCount(context);
        long partCount = model.getPartCount(context);
        String layer = model.getOwner().hyperTalkName;

        buttonLabel.setText(layer + " Button:");
        buttonLabelValue.setText(buttonNumber + " of " + buttonCount);

        partLabel.setText(layer + " Part:");
        partLabelValue.setText(partNumber + " of " + partCount);
        idLabelValue.setText(String.valueOf(model.getId()));

        buttonName.setText(model.get(context, ButtonModel.PROP_NAME).toString());
        buttonTop.setValue(model.get(context, ButtonModel.PROP_TOP).integerValue());
        buttonLeft.setValue(model.get(context, ButtonModel.PROP_LEFT).integerValue());
        buttonHeight.setValue(model.get(context, ButtonModel.PROP_HEIGHT).integerValue());
        buttonWidth.setValue(model.get(context, ButtonModel.PROP_WIDTH).integerValue());
        isEnabled.setSelected(model.get(context, ButtonModel.PROP_ENABLED).booleanValue());
        isShowTitle.setSelected(model.get(context, ButtonModel.PROP_SHOWNAME).booleanValue());
        isVisible.setSelected(model.get(context, ButtonModel.PROP_VISIBLE).booleanValue());
        style.setSelectedItem(ButtonStyle.fromName(model.get(context, ButtonModel.PROP_STYLE).toString()));
        family.setSelectedItem(model.get(context, ButtonModel.PROP_FAMILY).toString());
        autoHilite.setSelected(model.get(context, ButtonModel.ALIAS_AUTOHILIGHT).booleanValue());
        sharedHilite.setSelected(model.get(context, ButtonModel.PROP_SHAREDHILITE).booleanValue());

        // Shared hilite option only available on background buttons
        sharedHilite.setEnabled(model.getOwner() != Owner.CARD);

        bindActions(a -> updateProperties(),
                isEnabled,
                isShowTitle,
                isVisible,
                style,
                sharedHilite,
                buttonName,
                buttonHeight,
                buttonLeft,
                buttonTop,
                buttonWidth);
    }

    private void updateProperties() {
        ExecutionContext context = new ExecutionContext();

        model.set(context, ButtonModel.PROP_NAME, new Value(buttonName.getText()));
        model.set(context, ButtonModel.PROP_TOP, new Value(buttonTop.getValue()));
        model.set(context, ButtonModel.PROP_LEFT, new Value(buttonLeft.getValue()));
        model.set(context, ButtonModel.PROP_HEIGHT, new Value(buttonHeight.getValue()));
        model.set(context, ButtonModel.PROP_WIDTH, new Value(buttonWidth.getValue()));
        model.set(context, ButtonModel.PROP_ENABLED, new Value(isEnabled.isSelected()));
        model.set(context, ButtonModel.PROP_SHOWNAME, new Value(isShowTitle.isSelected()));
        model.set(context, ButtonModel.PROP_VISIBLE, new Value(isVisible.isSelected()));
        model.set(context, ButtonModel.PROP_STYLE, new Value(String.valueOf(style.getSelectedItem())));
        model.set(context, ButtonModel.PROP_FAMILY, new Value(String.valueOf(family.getSelectedItem())));
        model.set(context, ButtonModel.ALIAS_AUTOHILIGHT, new Value(autoHilite.isSelected()));
        model.set(context, ButtonModel.PROP_SHAREDHILITE, new Value(sharedHilite.isSelected()));
    }

    @RunOnDispatch
    private void showContentsEditor() {
        dispose();
        String contents = PartContentsEditor.editContents(model.get(new ExecutionContext(), PartModel.PROP_CONTENTS).toString(), getWindowPanel());
        if (contents != null) {
            model.set(new ExecutionContext(), PartModel.PROP_CONTENTS, new Value(contents));
        }
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
        buttonEditor.setLayout(new GridLayoutManager(5, 7, new Insets(10, 10, 10, 10), -1, -1));
        buttonEditor.setMaximumSize(new Dimension(587, 257));
        coordinatePanel = new JPanel();
        coordinatePanel.setLayout(new GridLayoutManager(4, 6, new Insets(5, 5, 5, 5), -1, -1));
        buttonEditor.add(coordinatePanel, new GridConstraints(0, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        coordinatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Identification"));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(4);
        label1.setText("Name:");
        coordinatePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(90, -1), null, new Dimension(90, -1), 0, false));
        buttonName = new JTextField();
        coordinatePanel.add(buttonName, new GridConstraints(0, 1, 1, 5, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonLabel = new JLabel();
        buttonLabel.setText("Card Button:");
        coordinatePanel.add(buttonLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonLabelValue = new JLabel();
        buttonLabelValue.setText("Label");
        coordinatePanel.add(buttonLabelValue, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partLabel = new JLabel();
        partLabel.setText("Card Part:");
        coordinatePanel.add(partLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partLabelValue = new JLabel();
        partLabelValue.setText("Label");
        coordinatePanel.add(partLabelValue, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        coordinatePanel.add(spacer1, new GridConstraints(2, 2, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Button ID:");
        coordinatePanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idLabelValue = new JLabel();
        idLabelValue.setText("Label");
        coordinatePanel.add(idLabelValue, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 4, new Insets(5, 5, 5, 5), -1, -1));
        buttonEditor.add(panel1, new GridConstraints(1, 0, 2, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Look and Feel"));
        style = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        style.setModel(defaultComboBoxModel1);
        panel1.add(style, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panel1.add(family, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Style:");
        panel1.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Family:");
        panel1.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoHilite = new JCheckBox();
        autoHilite.setMargin(new Insets(0, 1, 0, 1));
        autoHilite.setText("Auto Hilite");
        autoHilite.setToolTipText("Automatically hilite this button when the user clicks on it.");
        panel1.add(autoHilite, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isVisible = new JCheckBox();
        isVisible.setMargin(new Insets(0, 1, 0, 1));
        isVisible.setText("Visible");
        isVisible.setToolTipText("Hide or show this button on the card.");
        panel1.add(isVisible, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isShowTitle = new JCheckBox();
        isShowTitle.setMargin(new Insets(0, 1, 0, 1));
        isShowTitle.setText("Show Name");
        isShowTitle.setToolTipText("Display the button's name inside the part.");
        panel1.add(isShowTitle, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        isEnabled = new JCheckBox();
        isEnabled.setMargin(new Insets(0, 1, 0, 1));
        isEnabled.setText("Enabled");
        isEnabled.setToolTipText("Enable or disable (grey-out) the button.");
        panel1.add(isEnabled, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sharedHilite = new JCheckBox();
        sharedHilite.setText("Shared Hilite");
        panel1.add(sharedHilite, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editScriptButton = new JButton();
        editScriptButton.setText("Edit Script...");
        editScriptButton.setToolTipText("");
        buttonEditor.add(editScriptButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textStyle = new JButton();
        textStyle.setText("Text Style...");
        buttonEditor.add(textStyle, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contents = new JButton();
        contents.setText("Contents...");
        buttonEditor.add(contents, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("OK");
        buttonEditor.add(saveButton, new GridConstraints(4, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        buttonEditor.add(spacer3, new GridConstraints(4, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        buttonEditor.add(spacer4, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        iconButton = new JButton();
        iconButton.setText("Icon...");
        buttonEditor.add(iconButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 5, new Insets(5, 5, 5, 5), -1, -1));
        buttonEditor.add(panel2, new GridConstraints(1, 4, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Location"));
        final JLabel label5 = new JLabel();
        label5.setText("Width:");
        panel2.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonHeight = new JSpinner();
        panel2.add(buttonHeight, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), new Dimension(75, -1), 0, false));
        buttonWidth = new JSpinner();
        panel2.add(buttonWidth, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), new Dimension(75, -1), 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Top:");
        panel2.add(label6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Left:");
        panel2.add(label7, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonTop = new JSpinner();
        panel2.add(buttonTop, new GridConstraints(0, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), new Dimension(75, -1), 0, false));
        buttonLeft = new JSpinner();
        panel2.add(buttonLeft, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), new Dimension(75, -1), 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Height:");
        panel2.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        panel2.add(spacer5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        buttonEditor.add(spacer6, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return buttonEditor;
    }

}
