package com.defano.hypertalk.ast.expressions.operators;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.unary.NegateOp;
import com.defano.hypertalk.ast.expressions.operators.unary.NotOp;
import com.defano.hypertalk.ast.expressions.operators.unary.ThereIsAOp;
import com.defano.hypertalk.ast.expressions.operators.unary.ThereIsNoOp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class UnaryOperatorExp extends Expression {

    public final Expression rhs;
    
    public UnaryOperatorExp(ParserRuleContext context, Expression rhs) {
        super(context);
        this.rhs = rhs;
    }

    public Value rhs() throws HtException {
        return rhs.evaluate();
    }

    public static UnaryOperatorExp forOperator(ParserRuleContext ctx, UnaryOperator op, Expression rhs) {
        switch (op) {
            case NOT:
                return new NotOp(ctx, rhs);
            case NEGATE:
                return new NegateOp(ctx, rhs);
            case THERE_IS_A:
                return new ThereIsAOp(ctx, rhs);
            case THERE_IS_NOT_A:
                return new ThereIsNoOp(ctx, rhs);
        }

        throw new IllegalStateException("Bug! Unimplemented unary operator.");
    }
}
