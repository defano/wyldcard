package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.BinaryOperator;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class BinaryOperatorExp extends Expression {

    public final BinaryOperator operator;
    public final Expression lhs;
    public final Expression rhs;

    public BinaryOperatorExp(ParserRuleContext context, Expression lhs, BinaryOperator op, Expression rhs) {
        super(context);
        this.lhs = lhs;
        this.operator = op;
        this.rhs = rhs;
    }

    public Value onEvaluate() throws HtException {
        Value lhs = this.lhs.evaluate();
        Value rhs = this.rhs.evaluate();

        switch (operator) {
        case EQUALS: return new Value(lhs.equals(rhs));
        case NOT_EQUALS: return new Value(!lhs.equals(rhs));
        case LESS_THAN: return lhs.lessThan(rhs);
        case GREATER_THAN: return lhs.greaterThan(rhs);
        case LESS_THAN_OR_EQUALS: return lhs.lessThanOrEqualTo(rhs);
        case GREATER_THAN_OR_EQUALS: return lhs.greaterThanOrEqualTo(rhs);
        case PLUS: return lhs.add(rhs);
        case MINUS: return lhs.subtract(rhs);
        case MULTIPLY: return lhs.multiply(rhs);
        case DIVIDE: return lhs.divide(rhs);
        case MOD: return lhs.mod(rhs);
        case EXP: return lhs.exponentiate(rhs);
        case AND: return lhs.and(rhs);
        case OR: return lhs.or(rhs);
        case CONTAINS: return new Value(lhs.contains(rhs));
        case IS_IN: return new Value(rhs.contains(lhs));
        case IS_NOT_IN: return new Value(!rhs.contains(lhs));
        case AMP: return lhs.concat(rhs);
        case AMP_AMP: return lhs.concat(new Value(" ").concat(rhs));
        case IS_WITHIN: return lhs.within(rhs);
        case IS_NOT_WITHIN: return new Value(!lhs.within(rhs).booleanValue());
        case IS_A: return new Value(lhs.isA(rhs));
        case IS_NOT_A: return new Value(!lhs.isA(rhs).booleanValue());

        default: throw new HtSemanticException("Bug! Unimplemented binary operator in evaluation " + operator);
        }
    }
}
