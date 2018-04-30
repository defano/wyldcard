package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.menubar.main.HyperCardMenuBar;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class ResetMenuCmd extends Command {

    public ResetMenuCmd(ParserRuleContext context) {
        super(context, "reset");
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        HyperCardMenuBar.getInstance().reset();
    }
}
