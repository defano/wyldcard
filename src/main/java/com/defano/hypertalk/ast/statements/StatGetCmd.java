/*
 * StatGetCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * StatGetCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "get" statement
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class StatGetCmd extends Statement {

    public final Expression expression;
    public final PartSpecifier part;
    
    public StatGetCmd (Expression e) {
        expression = e;
        part = null;
    }
    
    public StatGetCmd (PartSpecifier ps) {
        expression = null;
        part = ps;
    }
    
    public void execute () throws HtSemanticException {
        if (expression != null) {
            GlobalContext.getContext().setIt(expression.evaluate());
        }
    }
}
