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

import com.defano.hypercard.runtime.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

public class GlobalStatement extends Statement {

    public final String symbol;
    
    public GlobalStatement(ParserRuleContext context, String symbol) {
        super(context);
        this.symbol = symbol;
    }

    public void onExecute() {
        ExecutionContext.getContext().defineGlobal(symbol);
    }
}
