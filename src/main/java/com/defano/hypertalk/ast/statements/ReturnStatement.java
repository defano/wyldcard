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
import com.defano.hypertalk.ast.breakpoints.TerminateHandlerBreakpoint;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public class ReturnStatement extends Statement {

    public final Expression returnValue;
    
    public ReturnStatement(ParserRuleContext context) {
        super(context);
        this.returnValue = new LiteralExp(null, "");
    }
    
    public ReturnStatement(ParserRuleContext context, Expression returnValue) {
        super(context);
        this.returnValue = returnValue;
    }

    public void onExecute() throws HtSemanticException, TerminateHandlerBreakpoint {
        ExecutionContext.getContext().setReturnValue(returnValue.evaluate());
        throw new TerminateHandlerBreakpoint(null);
    }
}
