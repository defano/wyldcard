package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.FileContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class CloseCmd extends Command {

    private final Expression filename;

    public CloseCmd(Expression filename) {
        super("close");
        this.filename = filename;
    }

    @Override
    public void onExecute() throws HtException {
        FileContext.getInstance().close(filename.evaluate().stringValue());
    }
}
