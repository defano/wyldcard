package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.awt.RoboticTypist;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class TypeCmd extends Command {

    public final Expression expression;
    public final boolean withCommandKey;

    public TypeCmd (Expression expression, boolean withCommandKey) {
        super("type");

        this.expression = expression;
        this.withCommandKey = withCommandKey;
    }

    @Override
    public void onExecute() throws HtException {
        String stringToType = expression.evaluate().stringValue();
        RoboticTypist.getInstance().type(stringToType, withCommandKey);
    }
}
