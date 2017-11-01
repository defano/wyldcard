package com.defano.hypercard.window.forms;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.util.StringUtils;
import com.defano.hypercard.window.HyperCardDialog;
import com.defano.hypercard.window.WindowBuilder;
import com.defano.hypercard.parts.field.FieldStyle;
import com.defano.hypercard.parts.card.CardLayerPartModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.parts.field.FieldModel;
import com.defano.hypertalk.ast.common.Owner;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.l2fprod.common.swing.JFontChooser;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unchecked")
public class FieldPropertyEditor extends HyperCardDialog {

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
    private JButton editScriptButton;
    private JComboBox style;
    private JCheckBox showLines;
    private JLabel fieldLabel;
    private JLabel partLabel;
    private JLabel partLabelValue;
    private JLabel fieldLabelValue;
    private JButton textStyleButton;
    private JCheckBox sharedText;
    private JCheckBox enabled;
    private JCheckBox isWideMargins;
    private JCheckBox autoTab;
    private JCheckBox autoSelect;
    private JCheckBox multipleLines;
    private JCheckBox scrolling;

    public FieldPropertyEditor() {
        editScriptButton.addActionListener(e -> {
            dispose();
            WindowBuilder.make(new ScriptEditor())
                    .withTitle("Script of field " + fieldName.getText())
                    .withModel(model)
                    .withLocationStaggeredOver(WindowManager.getStackWindow().getWindowPanel())
                    .resizeable(true)
                    .build();
        });

        saveButton.addActionListener(e -> {
            updateProperties();
            dispose();
        });

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (FieldStyle thisStyle : FieldStyle.values()) {
            model.addElement(thisStyle.getName());
        }
        style.setModel(model);

        enabled.addActionListener(e -> onEnabledChanged());
        autoSelect.addActionListener(e -> onAutoSelectChanged());
    }

    @Override
    public JButton getDefaultButton() {
        return saveButton;
    }

    @Override
    public JPanel getWindowPanel() {
        return fieldEditor;
    }

    @Override
    public void bindModel(Object data) {
        if (data instanceof PartModel) {
            this.model = (PartModel) data;

            PartModel part = HyperCard.getInstance().getDisplayedCard().findPartOnCard(model.getType(), model.getKnownProperty(PartModel.PROP_ID).integerValue());
            long partNumber = HyperCard.getInstance().getDisplayedCard().getPartNumber(part);
            long fieldNumber = HyperCard.getInstance().getDisplayedCard().getFieldNumber((FieldModel) part);
            long fieldCount = HyperCard.getInstance().getDisplayedCard().getPartCount(model.getType(), part.getOwner());
            long partCount = HyperCard.getInstance().getDisplayedCard().getPartCount(null, part.getOwner());
            String layer = part.getOwner().hyperTalkName;

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
            style.setSelectedItem(StringUtils.capitalize(model.getKnownProperty(FieldModel.PROP_STYLE).stringValue()));
            enabled.setSelected(model.getKnownProperty(FieldModel.PROP_ENABLED).booleanValue());
            isWideMargins.setSelected(model.getKnownProperty(FieldModel.PROP_WIDEMARGINS).booleanValue());
            autoTab.setSelected(model.getKnownProperty(FieldModel.PROP_AUTOTAB).booleanValue());
            autoSelect.setSelected(model.getKnownProperty(FieldModel.PROP_AUTOSELECT).booleanValue());
            multipleLines.setSelected(model.getKnownProperty(FieldModel.PROP_MULTIPLELINES).booleanValue());
            scrolling.setSelected(model.getKnownProperty(FieldModel.PROP_SCROLLING).booleanValue());

            sharedText.setEnabled(part.getOwner() == Owner.BACKGROUND);
            sharedText.setSelected(model.getKnownProperty(FieldModel.PROP_SHAREDTEXT).booleanValue());
            multipleLines.setEnabled(model.getKnownProperty(FieldModel.PROP_AUTOSELECT).booleanValue());

            textStyleButton.addActionListener(e -> {
                Font selection = JFontChooser.showDialog(getWindowPanel(), "Choose Font", ((CardLayerPartModel) model).getTextStyle().toFont());
                if (selection != null) {
                    ((FieldModel) part).setTextStyle(TextStyleSpecifier.fromFont(selection));
                }
            });

            onEnabledChanged();
            onAutoSelectChanged();

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
        model.setKnownProperty(FieldModel.PROP_STYLE, new Value(String.valueOf(style.getSelectedItem())));
        model.setKnownProperty(FieldModel.PROP_SHAREDTEXT, new Value(sharedText.isSelected()));
        model.setKnownProperty(FieldModel.PROP_ENABLED, new Value(enabled.isSelected()));
        model.setKnownProperty(FieldModel.PROP_WIDEMARGINS, new Value(isWideMargins.isSelected()));
        model.setKnownProperty(FieldModel.PROP_AUTOTAB, new Value(autoTab.isSelected()));
        model.setKnownProperty(FieldModel.PROP_AUTOSELECT, new Value(autoSelect.isSelected()));
        model.setKnownProperty(FieldModel.PROP_MULTIPLELINES, new Value(multipleLines.isSelected()));
        model.setKnownProperty(FieldModel.PROP_SCROLLING, new Value(scrolling.isSelected()));
    }

    private void onEnabledChanged() {
        if (!enabled.isSelected()) {
            isLockText.setSelected(true);
            isLockText.setEnabled(false);
        } else {
            isLockText.setEnabled(true);
        }
    }

    private void onAutoSelectChanged() {
        if (autoSelect.isSelected()) {
            isWrapText.setSelected(true);
            isWrapText.setEnabled(false);
            multipleLines.setEnabled(true);
        } else {
            isWrapText.setEnabled(true);
            multipleLines.setEnabled(false);
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        fieldEditor = new JPanel();
        fieldEditor.setLayout(new GridLayoutManager(5, 9, new Insets(10, 10, 10, 10), -1, -1));
        fieldEditor.setMaximumSize(new Dimension(587, 257));
        panel1.add(fieldEditor, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 5, new Insets(5, 5, 5, 5), -1, -1));
        fieldEditor.add(panel2, new GridConstraints(0, 0, 1, 9, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Identification"));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(4);
        label1.setText("Field Name:");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(90, -1), null, new Dimension(90, -1), 0, false));
        fieldLabel = new JLabel();
        fieldLabel.setText("Card Field:");
        panel2.add(fieldLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partLabel = new JLabel();
        partLabel.setText("Card Part:");
        panel2.add(partLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        partLabelValue = new JLabel();
        partLabelValue.setText("Label");
        panel2.add(partLabelValue, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldLabelValue = new JLabel();
        fieldLabelValue.setText("Label");
        panel2.add(fieldLabelValue, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldName = new JTextField();
        panel2.add(fieldName, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(2);
        label2.setInheritsPopupMenu(false);
        label2.setText("Field ID:");
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        idLabelValue = new JLabel();
        idLabelValue.setText("Label");
        panel2.add(idLabelValue, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(7, 3, new Insets(5, 5, 5, 5), -1, -1));
        fieldEditor.add(panel3, new GridConstraints(1, 0, 2, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Look and Feel"));
        isWrapText = new JCheckBox();
        isWrapText.setText("Don't Wrap");
        isWrapText.setToolTipText("Do not wrap long lines; scroll horizontally instead.");
        panel3.add(isWrapText, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isLockText = new JCheckBox();
        isLockText.setText("Lock Text");
        isLockText.setToolTipText("Make the text of this field uneditable to the user.");
        panel3.add(isLockText, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        style = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        style.setModel(defaultComboBoxModel1);
        panel3.add(style, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Style:");
        panel3.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isVisible = new JCheckBox();
        isVisible.setText("Visible");
        isVisible.setToolTipText("Hide or show this field on the card.");
        panel3.add(isVisible, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showLines = new JCheckBox();
        showLines.setText("Show Lines");
        showLines.setToolTipText("Draw dottled rule underneath lines of text.");
        panel3.add(showLines, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        sharedText = new JCheckBox();
        sharedText.setText("Shared Text");
        sharedText.setToolTipText("Share the text of this field across all cards in this background. (Applies only to background fields.)");
        panel3.add(sharedText, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enabled = new JCheckBox();
        enabled.setText("Enabled");
        enabled.setToolTipText("Enable or disable (grey-out) this field.");
        panel3.add(enabled, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        isWideMargins = new JCheckBox();
        isWideMargins.setText("Wide Margins");
        isWideMargins.setToolTipText("Inset the text 15px from the edges of the field.");
        panel3.add(isWideMargins, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoTab = new JCheckBox();
        autoTab.setText("Auto Tab");
        autoTab.setToolTipText("Transfer focus to the next part when the tab key is pressed.");
        panel3.add(autoTab, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoSelect = new JCheckBox();
        autoSelect.setText("Auto Select");
        autoSelect.setToolTipText("Automatically select the entire line of text that was clicked; makes this a \"list field.\"");
        panel3.add(autoSelect, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        multipleLines = new JCheckBox();
        multipleLines.setText("Multiple Lines");
        multipleLines.setToolTipText("Applies only to \"Auto Select\"; allows multiple lines to be selected together.");
        panel3.add(multipleLines, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrolling = new JCheckBox();
        scrolling.setText("Scrolling");
        scrolling.setToolTipText("Allow the field to scroll vertically if text exceeds visual bounds.");
        panel3.add(scrolling, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editScriptButton = new JButton();
        editScriptButton.setText("Edit Script...");
        fieldEditor.add(editScriptButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textStyleButton = new JButton();
        textStyleButton.setEnabled(true);
        textStyleButton.setText("Text Style...");
        fieldEditor.add(textStyleButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("OK");
        fieldEditor.add(saveButton, new GridConstraints(4, 8, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        fieldEditor.add(spacer2, new GridConstraints(4, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        fieldEditor.add(spacer3, new GridConstraints(4, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 4, new Insets(5, 5, 5, 5), -1, -1));
        fieldEditor.add(panel4, new GridConstraints(1, 6, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Location"));
        final JLabel label4 = new JLabel();
        label4.setText("Height:");
        panel4.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Width:");
        panel4.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldHeight = new JTextField();
        panel4.add(fieldHeight, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        fieldWidth = new JTextField();
        panel4.add(fieldWidth, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Top:");
        panel4.add(label6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Left:");
        panel4.add(label7, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldTop = new JTextField();
        panel4.add(fieldTop, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        fieldLeft = new JTextField();
        panel4.add(fieldLeft, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), new Dimension(50, -1), 0, false));
        final Spacer spacer4 = new Spacer();
        fieldEditor.add(spacer4, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }
}
