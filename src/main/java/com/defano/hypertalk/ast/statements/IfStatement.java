/*
 * StatIf
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * IfStatement.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of an if-then-else statement
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.constructs.ThenElseBlock;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class IfStatement extends Statement {

    public final Expression condition;
    public final ThenElseBlock then;
    
    public IfStatement(ParserRuleContext context, Expression condition, ThenElseBlock then) {
        super(context);
        this.condition = condition;
        this.then = then;
    }
    
    public void onExecute() throws HtException, Breakpoint {
        if (condition.evaluate().booleanValue())
            then.thenBranch.execute();
        else
            then.elseBranch.execute();
    }
}
