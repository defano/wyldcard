package com.defano.hypercard.parts.msgbox;

import com.defano.hypercard.parts.field.AddressableSelection;
import com.defano.hypercard.parts.field.SelectableTextModel;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;

import javax.swing.text.JTextComponent;

public class MsgBoxModel extends PartModel implements AddressableSelection, SelectableTextModel {

    public MsgBoxModel() {
        super(PartType.MESSAGE_BOX, Owner.HYPERCARD, null);

        defineProperty(PROP_ID, new Value(0), true);
        defineProperty(PROP_CONTENTS, new Value(), false);
        defineComputedGetterProperty(PROP_CONTENTS, (model, propertyName) -> new Value(WindowManager.getMessageWindow().getMsgBoxText()));

        defineProperty(PROP_WIDTH, new Value(WindowManager.getMessageWindow().getWindow().getWidth()), true);
        defineProperty(PROP_HEIGHT, new Value(WindowManager.getMessageWindow().getWindow().getHeight()), true);
        defineProperty(PROP_NAME, new Value("Message"), true);

        defineComputedGetterProperty(PartModel.PROP_LEFT, (model, propertyName) -> new Value(WindowManager.getMessageWindow().getWindow().getLocation().x));
        defineComputedSetterProperty(PartModel.PROP_LEFT, (model, propertyName, value) -> WindowManager.getMessageWindow().getWindow().setLocation(value.integerValue(), WindowManager.getMessageWindow().getWindow().getY()));

        defineComputedGetterProperty(PartModel.PROP_TOP, (model, propertyName) -> new Value(WindowManager.getMessageWindow().getWindow().getLocation().y));
        defineComputedSetterProperty(PartModel.PROP_TOP, (model, propertyName, value) -> WindowManager.getMessageWindow().getWindow().setLocation(WindowManager.getMessageWindow().getWindow().getX(), value.integerValue()));

        defineComputedGetterProperty(PROP_VISIBLE, (model, propertyName) -> new Value(WindowManager.getMessageWindow().isVisible()));
        defineComputedSetterProperty(PROP_VISIBLE, (model, propertyName, value) -> WindowManager.getMessageWindow().setVisible(value.booleanValue()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectableTextModel getSelectableTextModel() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelection(Range selection) {
        JTextComponent messageBox = WindowManager.getMessageWindow().getTextComponent();
        messageBox.setSelectionStart(selection.start);
        messageBox.setSelectionEnd(selection.end);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Range getSelection() {
        JTextComponent messageBox = WindowManager.getMessageWindow().getTextComponent();
        return Range.ofMarkAndDot(messageBox.getSelectionStart(), messageBox.getSelectionEnd());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        JTextComponent messageBox = WindowManager.getMessageWindow().getTextComponent();
        return messageBox.getText();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewDidUpdateSelection(Range selection) {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHyperTalkAddress() {
        return "the message";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartSpecifier getPartSpecifier() {
        return new PartMessageSpecifier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void relinkParentPartModel(PartModel parentPartModel) {
        // Nothing to do
    }
}
