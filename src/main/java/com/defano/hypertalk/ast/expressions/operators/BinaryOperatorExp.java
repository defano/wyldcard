package com.defano.hypertalk.ast.expressions.operators;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.operators.binary.*;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class BinaryOperatorExp extends Expression {

    protected final Expression lhs;
    protected final Expression rhs;

    public BinaryOperatorExp(ParserRuleContext ctx, Expression lhs, Expression rhs) {
        super(ctx);

        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Value lhs(ExecutionContext context) throws HtException {
        return lhs.evaluate(context);
    }

    public Value rhs(ExecutionContext context) throws HtException {
        return rhs.evaluate(context);
    }

    public static BinaryOperatorExp forOperator(ParserRuleContext ctx, BinaryOperator op, Expression lhs, Expression rhs) {

        switch (op) {
            case EQUALS:
                return new EqualsOp(ctx, lhs, rhs);
            case NOT_EQUALS:
                return new NotEqualsOp(ctx, lhs, rhs);
            case LESS_THAN:
                return new LessThanOp(ctx, lhs, rhs);
            case GREATER_THAN:
                return new GreaterThanOp(ctx, lhs, rhs);
            case LESS_THAN_OR_EQUALS:
                return new LessThanOrEqualsOp(ctx, lhs, rhs);
            case GREATER_THAN_OR_EQUALS:
                return new GreaterThanOrEqualsOp(ctx, lhs, rhs);
            case PLUS:
                return new PlusOp(ctx, lhs, rhs);
            case MINUS:
                return new MinusOp(ctx, lhs, rhs);
            case MULTIPLY:
                return new MultiplyOp(ctx, lhs, rhs);
            case DIVIDE:
                return new DivideOp(ctx, lhs, rhs);
            case MOD:
                return new ModOp(ctx, lhs, rhs);
            case EXP:
                return new ExpOp(ctx, lhs, rhs);
            case AND:
                return new AndOp(ctx, lhs, rhs);
            case OR:
                return new OrOp(ctx, lhs, rhs);
            case CONTAINS:
                return new ContainsOp(ctx, lhs, rhs);
            case IS_IN:
                return new IsInOp(ctx, lhs, rhs);
            case IS_NOT_IN:
                return new IsNotInOp(ctx, lhs, rhs);
            case AMP:
                return new AmpOp(ctx, lhs, rhs);
            case AMP_AMP:
                return new AmpAmpOp(ctx, lhs, rhs);
            case IS_WITHIN:
                return new IsWithinOp(ctx, lhs, rhs);
            case IS_NOT_WITHIN:
                return new IsNotWithinOp(ctx, lhs, rhs);
            case IS_A:
                return new IsAOp(ctx, lhs, rhs);
            case IS_NOT_A:
                return new IsNotAOp(ctx, lhs, rhs);
        }

        throw new IllegalStateException("Bug! Unimplemented binary operator.");
    }

}
