package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.util.RoboticTypist;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class TypeCmd extends Statement {

    public final Expression expression;
    public final boolean withCommandKey;

    public TypeCmd (Expression expression, boolean withCommandKey) {
        this.expression = expression;
        this.withCommandKey = withCommandKey;
    }

    @Override
    public void execute() throws HtException {
        String stringToType = expression.evaluate().stringValue();
        RoboticTypist.getInstance().type(stringToType, withCommandKey);
    }
}
