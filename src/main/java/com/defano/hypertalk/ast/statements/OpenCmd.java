package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.FileContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class OpenCmd extends Statement {

    private final Expression file;

    public OpenCmd(Expression file) {
        this.file = file;
    }

    @Override
    public void execute() throws HtException {
        FileContext.getInstance().open(file.evaluate().stringValue());
    }
}
