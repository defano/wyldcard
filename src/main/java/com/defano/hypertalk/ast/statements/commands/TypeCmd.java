package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.awt.RoboticTypist;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class TypeCmd extends Command {

    public final Expression expression;
    public final boolean withCommandKey;

    public TypeCmd (ParserRuleContext context, Expression expression, boolean withCommandKey) {
        super(context, "type");

        this.expression = expression;
        this.withCommandKey = withCommandKey;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        String stringToType = expression.evaluate(context).stringValue();
        RoboticTypist.getInstance().type(stringToType, withCommandKey);
    }
}
