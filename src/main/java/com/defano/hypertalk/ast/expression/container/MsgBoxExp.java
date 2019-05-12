package com.defano.hypertalk.ast.expression.container;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.layout.MessageWindow;
import com.defano.hypertalk.ast.model.enums.Preposition;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.model.enums.PartType;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;

public class MsgBoxExp extends ContainerExp {

    @Inject
    private WindowManager windowManager;

    public MsgBoxExp(ParserRuleContext context) {
        super(context);
    }

    @Override
    public Value onEvaluate(ExecutionContext context) throws HtException {
        Value value = new Value(windowManager.getMessageWindow().getMsgBoxText());
        return chunkOf(context, value, getChunk());
    }

    @Override
    public void putValue(ExecutionContext context, Value value, Preposition preposition) throws HtException {
        Value destValue = new Value(windowManager.getMessageWindow().getMsgBoxText());

        // Operating on a chunk of the existing value
        if (getChunk() != null)
            destValue = Value.ofMutatedChunk(context, destValue, preposition, getChunk(), value);
        else
            destValue = Value.ofValue(destValue, preposition, value);

        windowManager.getMessageWindow().setMsgBoxText(destValue.toString());
        context.setIt(destValue);

        // If message is hidden, show it but don't focus it
        if (!windowManager.getMessageWindow().isVisible()) {
            SwingUtilities.invokeLater(() -> {
                MessageWindow message = windowManager.getMessageWindow();
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
