/*
 * ExpUnaryOperator
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:12 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpUnaryOperator.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a unary operation expression, for example: "not true"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.UnaryOperator;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpUnaryOperator extends Expression {

    public final UnaryOperator operator;
    public final Expression rhs;
    
    public ExpUnaryOperator (UnaryOperator op, Expression rhs) {
        this.operator = op;
        this.rhs = rhs;
    }
    
    public Value evaluate () throws HtSemanticException {
        Value rhs = this.rhs.evaluate();
        
        switch (operator) {
        case NOT: return rhs.not();
        case NEGATE: return rhs.negate();
        default: throw new HtSemanticException("Bug! Unimplemented unary operator in evaluation: " + operator);
        }
    }
}
