package com.defano.wyldcard.parts.msgbox;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.field.AddressableSelection;
import com.defano.wyldcard.parts.field.SelectableTextModel;
import com.defano.wyldcard.parts.model.DispatchComputedGetter;
import com.defano.wyldcard.parts.model.WindowProxyPartModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;

import javax.swing.text.JTextComponent;

public class MsgBoxModel extends WindowProxyPartModel implements AddressableSelection, SelectableTextModel {

    public MsgBoxModel() {
        super(WindowManager.getInstance().getMessageWindow());

        defineProperty(PROP_CONTENTS, new Value(), false);
        defineComputedGetterProperty(PROP_CONTENTS, (DispatchComputedGetter) (context, model, propertyName) -> new Value(getText(context)));
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
     * @param context The execution context.
     */
    @Override
    public Range getSelection(ExecutionContext context) {
        JTextComponent messageBox = WindowManager.getInstance().getMessageWindow().getTextComponent();
        return Range.ofMarkAndDot(messageBox.getSelectionStart(), messageBox.getSelectionEnd());
    }

    /**
     * {@inheritDoc}
     * @param context The execution context.
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
     * @param context The execution context.
     */
    @Override
    public String getHyperTalkAddress(ExecutionContext context) {
        return "the message";
    }

    /**
     * {@inheritDoc}
     * @param context The execution context.
     */
    @Override
    public PartSpecifier getPartSpecifier(ExecutionContext context) {
        return new PartMessageSpecifier();
    }
}
