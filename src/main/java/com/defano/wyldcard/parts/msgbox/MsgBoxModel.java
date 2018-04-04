package com.defano.wyldcard.parts.msgbox;

import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.field.AddressableSelection;
import com.defano.wyldcard.parts.field.SelectableTextModel;
import com.defano.wyldcard.parts.model.DispatchComputedGetter;
import com.defano.wyldcard.parts.model.DispatchComputedSetter;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;
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
        defineComputedGetterProperty(PROP_CONTENTS, (DispatchComputedGetter) (context, model, propertyName) -> new Value(getText(context)));

        defineProperty(PROP_WIDTH, new Value(WindowManager.getInstance().getMessageWindow().getWindow().getWidth()), true);
        defineProperty(PROP_HEIGHT, new Value(WindowManager.getInstance().getMessageWindow().getWindow().getHeight()), true);
        defineProperty(PROP_NAME, new Value("Message"), true);

        defineComputedGetterProperty(PartModel.PROP_LEFT, (DispatchComputedGetter) (context, model, propertyName) -> new Value(WindowManager.getInstance().getMessageWindow().getWindow().getLocation().x));
        defineComputedSetterProperty(PartModel.PROP_LEFT, (DispatchComputedSetter) (context, model, propertyName, value) -> WindowManager.getInstance().getMessageWindow().getWindow().setLocation(value.integerValue(), WindowManager.getInstance().getMessageWindow().getWindow().getY()));

        defineComputedGetterProperty(PartModel.PROP_TOP, (DispatchComputedGetter) (context, model, propertyName) -> new Value(WindowManager.getInstance().getMessageWindow().getWindow().getLocation().y));
        defineComputedSetterProperty(PartModel.PROP_TOP, (DispatchComputedSetter) (context, model, propertyName, value) -> WindowManager.getInstance().getMessageWindow().getWindow().setLocation(WindowManager.getInstance().getMessageWindow().getWindow().getX(), value.integerValue()));

        defineComputedGetterProperty(PROP_VISIBLE, (DispatchComputedGetter) (context, model, propertyName) -> new Value(WindowManager.getInstance().getMessageWindow().isVisible()));
        defineComputedSetterProperty(PROP_VISIBLE, (DispatchComputedSetter) (context, model, propertyName, value) -> WindowManager.getInstance().getMessageWindow().setVisible(value.booleanValue()));
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
    public void setSelection(ExecutionContext context, Range selection) {
        JTextComponent messageBox = WindowManager.getInstance().getMessageWindow().getTextComponent();
        messageBox.setSelectionStart(selection.start);
        messageBox.setSelectionEnd(selection.end);
    }

    /**
     * {@inheritDoc}
     * @param context
     */
    @Override
    public Range getSelection(ExecutionContext context) {
        JTextComponent messageBox = WindowManager.getInstance().getMessageWindow().getTextComponent();
        return Range.ofMarkAndDot(messageBox.getSelectionStart(), messageBox.getSelectionEnd());
    }

    /**
     * {@inheritDoc}
     * @param context
     */
    @Override
    @RunOnDispatch
    public String getText(ExecutionContext context) {
        JTextComponent messageBox = WindowManager.getInstance().getMessageWindow().getTextComponent();
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
     * @param context
     */
    @Override
    public String getHyperTalkAddress(ExecutionContext context) {
        return "the message";
    }

    /**
     * {@inheritDoc}
     * @param context
     */
    @Override
    public PartSpecifier getPartSpecifier(ExecutionContext context) {
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
