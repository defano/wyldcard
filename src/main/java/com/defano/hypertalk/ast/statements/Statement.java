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

import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class Statement {

    // Set by a return statement to indicate that the remainder of the statement
    // list should not execute. 
    public boolean breakExecution = false;
    
    public void execute() throws HtException {
        throw new HtSemanticException("Bug! Unimplemented execute() for statement.");
    }
}
