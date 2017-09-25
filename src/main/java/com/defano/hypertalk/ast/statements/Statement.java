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

import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

public class Statement {

    public void execute() throws HtException, Breakpoint {
        throw new HtSemanticException("Bug! Unimplemented execute() for statement.");
    }

}
