package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;
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
    public void onExecute(ExecutionContext context) throws HtException {
        WyldCard.getInstance().getFileManager().open(file.evaluate(context).toString());
    }
}
