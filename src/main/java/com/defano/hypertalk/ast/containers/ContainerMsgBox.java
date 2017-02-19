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

import com.defano.hypercard.context.GlobalContext;
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
        Value value = new Value(HyperCard.getInstance().getMsgBoxText());
        return chunkOf(value, this.chunk());
    }

    @Override
    public void putValue(Value value, Preposition preposition) throws HtException {
        GlobalContext.getContext().put(value, preposition, this);
        SwingUtilities.invokeLater(() -> WindowManager.getMessageWindow().setShown(true));
    }

    public PartType type() {
        return PartType.MESSAGEBOX;
    }
}
