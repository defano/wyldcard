package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.FileContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class CloseCmd extends Statement {

    private final Expression filename;

    public CloseCmd(Expression filename) {
        this.filename = filename;
    }

    @Override
    public void execute() throws HtException {
        FileContext.getInstance().close(filename.evaluate().stringValue());
    }
}
