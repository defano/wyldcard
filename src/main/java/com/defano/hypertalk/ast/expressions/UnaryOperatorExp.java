package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.UnaryOperator;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class UnaryOperatorExp extends Expression {

    public final UnaryOperator operator;
    public final Expression rhs;
    
    public UnaryOperatorExp(ParserRuleContext context, UnaryOperator op, Expression rhs) {
        super(context);
        this.operator = op;
        this.rhs = rhs;
    }
    
    public Value onEvaluate() throws HtException {
        Value rhs = this.rhs.evaluate();
        
        switch (operator) {
        case NOT: return rhs.not();
        case NEGATE: return rhs.negate();
        default: throw new HtSemanticException("Bug! Unimplemented unary operator in evaluation: " + operator);
        }
    }
}
