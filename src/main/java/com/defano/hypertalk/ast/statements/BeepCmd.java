package com.defano.hypertalk.ast.statements;

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
