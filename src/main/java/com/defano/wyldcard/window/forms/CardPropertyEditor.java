package com.defano.wyldcard.window.forms;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.util.StringUtils;
import com.defano.wyldcard.window.HyperCardDialog;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.card.CardModel;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.window.WindowManager;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

public class CardPropertyEditor extends HyperCardDialog {
    private CardModel cardModel;

    private JTextField cardName;
    private JCheckBox cardMarkedCheckBox;
    private JCheckBox cantDeleteCardCheckBox;
    private JButton saveButton;
    private JLabel buttonCountLabel;
    private JLabel fieldCountLabel;
    private JLabel cardIdLabel;
    private JLabel cardNumberLabel;
    private JPanel propertiesPanel;
    private JButton scriptButton;
    private JButton contentsButton;
    private JCheckBox dontSearchCheckBox;

    public CardPropertyEditor() {
        saveButton.addActionListener(e -> {
            dispose();
            updateProperties();
        });

        contentsButton.addActionListener(e -> {
            dispose();
            showContentsEditor();
        });

        scriptButton.addActionListener(e ->
                WindowBuilder.make(new ScriptEditor())
                        .withTitle("Script of " + cardModel.getKnownProperty(CardModel.PROP_NAME).stringValue())
                        .withModel(cardModel)
                        .resizeable(true)
                        .withLocationStaggeredOver(WindowManager.getInstance().getStackWindow().getWindowPanel())
                        .buildReplacing(this));
    }

    private void updateProperties() {
        cardModel.setKnownProperty(CardModel.PROP_NAME, new Value(cardName.getText()));
        cardModel.setKnownProperty(CardModel.PROP_MARKED, new Value(cardMarkedCheckBox.isSelected()));
        cardModel.setKnownProperty(CardModel.PROP_CANTDELETE, new Value(cantDeleteCardCheckBox.isSelected()));
        cardModel.setKnownProperty(CardModel.PROP_DONTSEARCH, new Value(dontSearchCheckBox.isSelected()));
    }

    @Override
    public JButton getDefaultButton() {
        return saveButton;
    }

    @Override
    public JPanel getWindowPanel() {
        return propertiesPanel;
    }

    @Override
    public void bindModel(Object data) {
        CardPart card = (CardPart) data;
        cardModel = card.getCardModel();

        // Don't display "default" name ('card id xxx')
        Value cardNameValue = cardModel.getRawProperty(CardModel.PROP_NAME);
        if (cardNameValue != null && !cardNameValue.isEmpty()) {
            cardName.setText(cardModel.getKnownProperty(CardModel.PROP_NAME).stringValue());
        }

        cardMarkedCheckBox.setSelected(cardModel.getKnownProperty(CardModel.PROP_MARKED).booleanValue());
        cantDeleteCardCheckBox.setSelected(cardModel.getKnownProperty(CardModel.PROP_CANTDELETE).booleanValue());
        dontSearchCheckBox.setSelected(cardModel.getKnownProperty(CardModel.PROP_DONTSEARCH).booleanValue());
        cardIdLabel.setText(String.valueOf(cardModel.getKnownProperty(CardModel.PROP_ID).stringValue()));

        long fieldCount = card.getCardModel().getPartCount(PartType.FIELD, Owner.CARD);
        long buttonCount = card.getCardModel().getPartCount(PartType.BUTTON, Owner.CARD);

        int cardNumber = WyldCard.getInstance().getActiveStackDisplayedCard().getCardModel().getCardIndexInStack() + 1;
        int cardCount = WyldCard.getInstance().getActiveStack().getCardCountProvider().blockingFirst();

        cardNumberLabel.setText(cardNumber + " out of " + cardCount);
        buttonCountLabel.setText(StringUtils.pluralize(buttonCount, "Contains %d card button.", "Contains %d card buttons."));
        fieldCountLabel.setText(StringUtils.pluralize(fieldCount, "Contains %d card field.", "Contains %d card fields."));
    }

    private void showContentsEditor() {
        String contents = PartContentsEditor.editContents(cardModel.getKnownProperty(PartModel.PROP_CONTENTS).stringValue(), getWindowPanel());
        if (contents != null) {
            cardModel.setKnownProperty(PartModel.PROP_CONTENTS, new Value(contents));
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
        propertiesPanel = new JPanel();
        propertiesPanel.setLayout(new GridLayoutManager(13, 3, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Card Name:");
        propertiesPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(166, 16), null, 0, false));
        cardName = new JTextField();
        propertiesPanel.add(cardName, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Card Number:");
        propertiesPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(166, 16), null, 0, false));
        cardNumberLabel = new JLabel();
        cardNumberLabel.setText("1 out of 1");
        propertiesPanel.add(cardNumberLabel, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Card ID:");
        propertiesPanel.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(166, 16), null, 0, false));
        cardIdLabel = new JLabel();
        cardIdLabel.setText("0000");
        propertiesPanel.add(cardIdLabel, new GridConstraints(2, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldCountLabel = new JLabel();
        fieldCountLabel.setText("Contains 2 card fields.");
        propertiesPanel.add(fieldCountLabel, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCountLabel = new JLabel();
        buttonCountLabel.setText("Contains 2 card buttons.");
        propertiesPanel.add(buttonCountLabel, new GridConstraints(5, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cardMarkedCheckBox = new JCheckBox();
        cardMarkedCheckBox.setText("Card Marked");
        propertiesPanel.add(cardMarkedCheckBox, new GridConstraints(7, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cantDeleteCardCheckBox = new JCheckBox();
        cantDeleteCardCheckBox.setText("Can't Delete Card");
        propertiesPanel.add(cantDeleteCardCheckBox, new GridConstraints(8, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("");
        propertiesPanel.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(166, 0), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("");
        propertiesPanel.add(label5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(166, 0), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("");
        propertiesPanel.add(label6, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(166, 0), null, 0, false));
        scriptButton = new JButton();
        scriptButton.setEnabled(true);
        scriptButton.setText("Edit Script...");
        scriptButton.setToolTipText("Not implemented");
        propertiesPanel.add(scriptButton, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(166, 32), null, 0, false));
        contentsButton = new JButton();
        contentsButton.setEnabled(true);
        contentsButton.setText("Contents...");
        contentsButton.setToolTipText("Not implemented");
        propertiesPanel.add(contentsButton, new GridConstraints(12, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(166, 32), null, 0, false));
        saveButton = new JButton();
        saveButton.setText("OK");
        propertiesPanel.add(saveButton, new GridConstraints(12, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        propertiesPanel.add(spacer1, new GridConstraints(12, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        dontSearchCheckBox = new JCheckBox();
        dontSearchCheckBox.setText("Don't Search Card");
        propertiesPanel.add(dontSearchCheckBox, new GridConstraints(9, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return propertiesPanel;
    }
}
