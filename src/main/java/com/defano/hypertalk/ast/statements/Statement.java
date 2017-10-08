/*
 * Statement
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * Statement.java
 * @author matt.defano@gmail.com
 * 
 * Superclass of all statements
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Statement extends ASTNode {

    public Statement(ParserRuleContext context) {
        super(context);
    }

    protected abstract void onExecute() throws HtException, Breakpoint;

    public void execute() throws HtException, Breakpoint {
        try {
            onExecute();
        } catch (HtSemanticException e) {
            rethrowContextualizedException(e);
        }
    }
}
