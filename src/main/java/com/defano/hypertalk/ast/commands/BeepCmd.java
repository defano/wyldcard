package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;

import java.awt.*;

public class BeepCmd extends Command {

    public BeepCmd() {
        super("beep");
    }

    @Override
    public void onExecute() throws HtException {
        Toolkit.getDefaultToolkit().beep();
    }
}
