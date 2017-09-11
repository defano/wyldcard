/*
 * ContainerMsgBox
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ContainerMsgBox.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Representation of the message box as a container for Value
 */

package com.defano.hypertalk.ast.containers;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.ast.common.Chunk;
import com.defano.hypertalk.ast.common.PartType;

import javax.swing.*;

public class ContainerMsgBox extends Container {

    public final Chunk chunk;

    public ContainerMsgBox() {
        this.chunk = null;
    }

    public ContainerMsgBox(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk chunk() {
        return chunk;
    }

    @Override
    public Value getValue() throws HtException {
        Value value = new Value(HyperCard.getInstance().getMessageBoxText());
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        Value destValue = new Value(HyperCard.getInstance().getMessageBoxText());

        // Operating on a chunk of the existing value
        if (chunk != null)
            destValue = Value.setChunk(destValue, preposition, chunk, value);
        else
            destValue = Value.setValue(destValue, preposition, value);

        HyperCard.getInstance().setMessageBoxText(destValue);
        ExecutionContext.getContext().setIt(destValue);

        SwingUtilities.invokeLater(() -> WindowManager.getMessageWindow().setVisible(true));
    }

    public PartType type() {
        return PartType.MESSAGE_BOX;
    }
}
