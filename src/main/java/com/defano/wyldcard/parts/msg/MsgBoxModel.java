package com.defano.wyldcard.parts.msg;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartMessageSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.field.AddressableSelection;
import com.defano.wyldcard.parts.field.SelectableTextModel;
import com.defano.wyldcard.parts.model.WindowProxyPartModel;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.thread.Invoke;

import javax.swing.text.JTextComponent;

public class MsgBoxModel extends WindowProxyPartModel implements AddressableSelection, SelectableTextModel {

    public MsgBoxModel() {
        super(WyldCard.getInstance().getWindowManager().getMessageWindow());

        define(PROP_CONTENTS).asValue();
        findProperty(PROP_CONTENTS).value().applyOnGetTransform((context, model, rawValue) -> new Value(getText(context)));
        define(PROP_NUMBER).asComputedReadOnlyValue((context, model) -> WyldCard.getInstance().getWindowManager().getMessageWindow().getNumberOfWindow());
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
        JTextComponent messageBox = WyldCard.getInstance().getWindowManager().getMessageWindow().getTextComponent();
        messageBox.setSelectionStart(selection.start);
        messageBox.setSelectionEnd(selection.end);
    }

    /**
     * {@inheritDoc}
     * @param context The execution context.
     */
    @Override
    public Range getSelection(ExecutionContext context) {
        JTextComponent messageBox = WyldCard.getInstance().getWindowManager().getMessageWindow().getTextComponent();
        return Range.ofMarkAndDot(messageBox.getSelectionStart(), messageBox.getSelectionEnd());
    }

    /**
     * {@inheritDoc}
     * @param context The execution context.
     */
    @Override
    public String getText(ExecutionContext context) {
        return Invoke.onDispatch(() -> {
            JTextComponent messageBox = WyldCard.getInstance().getWindowManager().getMessageWindow().getTextComponent();
            return messageBox.getText();
        });
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
