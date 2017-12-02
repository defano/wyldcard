package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypercard.window.forms.MessageWindow;
import com.defano.hypertalk.ast.common.Preposition;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.PartType;

import javax.swing.*;

public class MsgBoxContainer extends Container {

    @Override
    public Value getValue() throws HtException {
        Value value = new Value(WindowManager.getMessageWindow().getMsgBoxText());
        return chunkOf(value, getChunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        Value destValue = new Value(WindowManager.getMessageWindow().getMsgBoxText());

        // Operating on a chunk of the existing value
        if (getChunk() != null)
            destValue = Value.setChunk(destValue, preposition, getChunk(), value);
        else
            destValue = Value.setValue(destValue, preposition, value);

        WindowManager.getMessageWindow().setMsgBoxText(destValue.stringValue());
        ExecutionContext.getContext().setIt(destValue);

        // If message is hidden, show it but don't focus it
        if (!WindowManager.getMessageWindow().isVisible()) {
            SwingUtilities.invokeLater(() -> {
                MessageWindow message = WindowManager.getMessageWindow();
                message.setFocusableWindowState(false);
                message.setVisible(true);
                message.setFocusableWindowState(true);
            });
        }
    }

    public PartType type() {
        return PartType.MESSAGE_BOX;
    }
}
