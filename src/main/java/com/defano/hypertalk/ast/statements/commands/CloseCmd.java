package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.FileContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class CloseCmd extends Command {

    private final Expression filename;

    public CloseCmd(ParserRuleContext context, Expression filename) {
        super(context, "close");
        this.filename = filename;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        FileContext.getInstance().close(filename.evaluate(context).stringValue());
    }
}
