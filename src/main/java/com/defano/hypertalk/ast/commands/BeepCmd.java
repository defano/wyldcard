package com.defano.hypertalk.ast.commands;

import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.awt.*;

public class BeepCmd extends Command {

    public BeepCmd(ParserRuleContext context) {
        super(context, "beep");
    }

    @Override
    public void onExecute() throws HtException {
        Toolkit.getDefaultToolkit().beep();
    }
}
