package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.model.specifiers.StackPartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class StackPartExp extends PartExp {

    private final Expression stackName;

    public StackPartExp(ParserRuleContext ctx) {
        this(ctx, null);
    }

    public StackPartExp(ParserRuleContext ctx, Expression stackName) {
        super(ctx);
        this.stackName = stackName;
    }

    @Override
    public PartSpecifier evaluateAsSpecifier(ExecutionContext context) throws HtException {
        if (stackName == null) {
            return new StackPartSpecifier();
        } else {
            return new StackPartSpecifier(stackName.evaluate(context).stringValue());
        }
    }
}
