package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.exception.HtException;

import java.awt.*;

public class BeepCmd extends Statement {

    @Override
    public void execute() throws HtException {
        Toolkit.getDefaultToolkit().beep();
    }
}
