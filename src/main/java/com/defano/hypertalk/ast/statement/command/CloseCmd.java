package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
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
        WyldCard.getInstance().getFileManager().close(filename.evaluate(context).toString());
    }
}
