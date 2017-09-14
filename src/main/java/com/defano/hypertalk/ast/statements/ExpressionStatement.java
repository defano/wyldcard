/*
 * StatExp
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpressionStatement.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of an expression statement
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.VariableExp;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpressionStatement extends Statement {

    public final Expression expression;
    
    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
    
    public void execute () throws HtSemanticException {

        // Special case: A variable name used as a statement should be interpreted as a message command
        if (expression instanceof VariableExp) {
            MessageCmd messageCmd = new MessageCmd(expression.evaluate().stringValue(), new ExpressionList());
            messageCmd.execute();
        }

        Value v = expression.evaluate();
        ExecutionContext.getContext().setIt(v);
    }
}
