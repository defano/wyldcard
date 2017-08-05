/*
 * StatGlobal
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * GlobalStatement.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the global variable declaration statement
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ExecutionContext;

public class GlobalStatement extends Statement {

    public final String symbol;
    
    public GlobalStatement(String symbol) {
        this.symbol = symbol;
    }

    public void execute () {
        ExecutionContext.getContext().defineGlobal(symbol);
    }
}
