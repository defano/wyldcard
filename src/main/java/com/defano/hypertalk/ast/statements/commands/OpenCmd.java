package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.FileContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class OpenCmd extends Command {

    private final Expression file;

    public OpenCmd(ParserRuleContext context, Expression file) {
        super(context, "open");
        this.file = file;
    }

    @Override
    public void onExecute() throws HtException {
        FileContext.getInstance().open(file.evaluate().stringValue());
    }
}
