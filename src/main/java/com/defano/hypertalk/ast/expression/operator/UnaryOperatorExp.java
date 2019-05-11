package com.defano.hypertalk.ast.expression.operator;

import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.expression.operator.unary.NegateOp;
import com.defano.hypertalk.ast.expression.operator.unary.NotOp;
import com.defano.hypertalk.ast.expression.operator.unary.ThereIsAOp;
import com.defano.hypertalk.ast.expression.operator.unary.ThereIsNoOp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class UnaryOperatorExp extends Expression {

    public final Expression rhs;
    
    public UnaryOperatorExp(ParserRuleContext context, Expression rhs) {
        super(context);
        this.rhs = rhs;
    }

    public Value rhs(ExecutionContext context) throws HtException {
        return rhs.evaluate(context);
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
