package com.defano.wyldcard.window.layouts;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.fonts.TextStyleSpecifier;
import com.defano.wyldcard.parts.field.FieldModel;
import com.defano.wyldcard.parts.field.FieldStyle;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.util.StringUtils;
import com.defano.wyldcard.window.ActionBindable;
import com.defano.wyldcard.window.WyldCardDialog;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.Value;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.l2fprod.common.swing.JFontChooser;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unchecked")
public class FieldPropertyEditor extends WyldCardDialog<FieldModel> implements ActionBindable {

    private FieldModel model;

    private JPanel fieldEditor;
    private JTextField fieldName;
    private JLabel idLabelValue;
    private JSpinner fieldHeight;
    private JSpinner fieldWidth;
    private JSpinner fieldTop;
    private JSpinner fieldLeft;
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
    private JCheckBox dontSearch;

    public FieldPropertyEditor() {
        editScriptButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> model.editScript(new ExecutionContext()));
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
    @RunOnDispatch
    public void bindModel(FieldModel data) {
        ExecutionContext context = new ExecutionContext();

        this.model = data;

        long partNumber = model.getPartNumber(context);
        long fieldNumber = model.getButtonOrFieldNumber(context);
        long fieldCount = model.getButtonOrFieldCount(context);
        long partCount = model.getPartCount(context);
        String layer = model.getOwner().hyperTalkName;

        fieldLabel.setText(layer + " Field:");
        fieldLabelValue.setText(fieldNumber + " of " + fieldCount);

        partLabel.setText(layer + " Part:");
        partLabelValue.setText(partNumber + " of " + partCount);
        idLabelValue.setText(String.valueOf(model.getId()));

        fieldName.setText(model.get(context, FieldModel.PROP_NAME).toString());
        idLabelValue.setText(model.get(context, FieldModel.PROP_ID).toString());
        fieldTop.setValue(model.get(context, FieldModel.PROP_TOP).integerValue());
        fieldLeft.setValue(model.get(context, FieldModel.PROP_LEFT).integerValue());
        fieldHeight.setValue(model.get(context, FieldModel.PROP_HEIGHT).integerValue());
        fieldWidth.setValue(model.get(context, FieldModel.PROP_WIDTH).integerValue());
        isLockText.setSelected(model.get(context, FieldModel.PROP_LOCKTEXT).booleanValue());
        isVisible.setSelected(model.get(context, FieldModel.PROP_VISIBLE).booleanValue());
        isWrapText.setSelected(model.get(context, FieldModel.PROP_DONTWRAP).booleanValue());
        showLines.setSelected(model.get(context, FieldModel.PROP_SHOWLINES).booleanValue());
        style.setSelectedItem(StringUtils.capitalize(model.get(context, FieldModel.PROP_STYLE).toString()));
        enabled.setSelected(model.get(context, FieldModel.PROP_ENABLED).booleanValue());
        isWideMargins.setSelected(model.get(context, FieldModel.PROP_WIDEMARGINS).booleanValue());
        autoTab.setSelected(model.get(context, FieldModel.PROP_AUTOTAB).booleanValue());
        autoSelect.setSelected(model.get(context, FieldModel.PROP_AUTOSELECT).booleanValue());
        multipleLines.setSelected(model.get(context, FieldModel.PROP_MULTIPLELINES).booleanValue());
        scrolling.setSelected(model.get(context, FieldModel.PROP_SCROLLING).booleanValue());
        dontSearch.setSelected(model.get(context, FieldModel.PROP_DONTSEARCH).booleanValue());
        sharedText.setEnabled(model.getOwner() == Owner.BACKGROUND);
        sharedText.setSelected(model.get(context, FieldModel.PROP_SHAREDTEXT).booleanValue());
        multipleLines.setEnabled(model.get(context, FieldModel.PROP_AUTOSELECT).booleanValue());

        textStyleButton.addActionListener(e -> {
            dispose();
            Font selection = JFontChooser.showDialog(getWindowPanel(), "Choose Font", model.getTextStyle(context).toFont());
            if (selection != null) {
                model.setTextStyle(context, TextStyleSpecifier.fromFont(selection));
            }
        });

        onEnabledChanged();
        onAutoSelectChanged();

        bindActions(a -> updateProperties(),
                fieldTop,
                fieldLeft,
                fieldHeight,
                fieldWidth,
                isWideMargins,
                isWrapText,
                showLines,
                style,
                enabled,
                autoSelect,
                multipleLines,
                dontSearch,
                scrolling);
    }

    private void updateProperties() {
        ExecutionContext context = new ExecutionContext();

        model.set(context, FieldModel.PROP_NAME, new Value(fieldName.getText()));
        model.set(context, FieldModel.PROP_TOP, new Value(fieldTop.getValue()));
        model.set(context, FieldModel.PROP_LEFT, new Value(fieldLeft.getValue()));
        model.set(context, FieldModel.PROP_HEIGHT, new Value(fieldHeight.getValue()));
        model.set(context, FieldModel.PROP_WIDTH, new Value(fieldWidth.getValue()));
        model.set(context, FieldModel.PROP_LOCKTEXT, new Value(isLockText.isSelected()));
        model.set(context, FieldModel.PROP_VISIBLE, new Value(isVisible.isSelected()));
        model.set(context, FieldModel.PROP_DONTWRAP, new Value(isWrapText.isSelected()));
        model.set(context, FieldModel.PROP_SHOWLINES, new Value(showLines.isSelected()));
        model.set(context, FieldModel.PROP_STYLE, new Value(String.valueOf(style.getSelectedItem())));
        model.set(context, FieldModel.PROP_SHAREDTEXT, new Value(sharedText.isSelected()));
        model.set(context, FieldModel.PROP_ENABLED, new Value(enabled.isSelected()));
        model.set(context, FieldModel.PROP_WIDEMARGINS, new Value(isWideMargins.isSelected()));
        model.set(context, FieldModel.PROP_AUTOTAB, new Value(autoTab.isSelected()));
        model.set(context, FieldModel.PROP_AUTOSELECT, new Value(autoSelect.isSelected()));
        model.set(context, FieldModel.PROP_MULTIPLELINES, new Value(multipleLines.isSelected()));
        model.set(context, FieldModel.PROP_SCROLLING, new Value(scrolling.isSelected()));
        model.set(context, FieldModel.PROP_DONTSEARCH, new Value(dontSearch.isSelected()));
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
            isLockText.setSelected(true);
            isLockText.setEnabled(false);
            isWrapText.setSelected(true);
            isWrapText.setEnabled(false);
            multipleLines.setEnabled(true);
        } else {
            isWrapText.setEnabled(true);
            multipleLines.setEnabled(false);
            isLockText.setEnabled(true);
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
        dontSearch = new JCheckBox();
        dontSearch.setText("Don't Search");
        panel3.add(dontSearch, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        fieldHeight = new JSpinner();
        panel4.add(fieldHeight, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), new Dimension(75, -1), 0, false));
        fieldWidth = new JSpinner();
        panel4.add(fieldWidth, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), new Dimension(75, -1), 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Top:");
        panel4.add(label6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Left:");
        panel4.add(label7, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldTop = new JSpinner();
        panel4.add(fieldTop, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), new Dimension(75, -1), 0, false));
        fieldLeft = new JSpinner();
        panel4.add(fieldLeft, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, -1), new Dimension(75, -1), 0, false));
        final Spacer spacer4 = new Spacer();
        fieldEditor.add(spacer4, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }
}
