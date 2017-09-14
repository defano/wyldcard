/*
 * StatReturn
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ReturnStatement.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "return" statement
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.exception.HtSemanticException;

public class ReturnStatement extends Statement {

    public final Expression returnValue;
    
    public ReturnStatement() {
        this.returnValue = new LiteralExp("");
    }
    
    public ReturnStatement(Expression returnValue) {
        this.returnValue = returnValue;
    }

    public void execute () throws HtSemanticException {
        ExecutionContext.getContext().setReturnValue(returnValue.evaluate());
        this.breakExecution = true;
    }
}
