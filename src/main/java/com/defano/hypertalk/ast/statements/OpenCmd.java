package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.FileContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class OpenCmd extends Command {

    private final Expression file;

    public OpenCmd(Expression file) {
        super("open");
        this.file = file;
    }

    @Override
    public void onExecute() throws HtException {
        FileContext.getInstance().open(file.evaluate().stringValue());
    }
}
