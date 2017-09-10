/*
 * StatDo
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * DoCmd.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the "do" statement (for executing strings)
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class DoCmd extends Command {

    public final Expression script;
    
    public DoCmd(Expression script) {
        super("do");
        this.script = script;
    }
    
    public void onExecute () throws HtException {
        Interpreter.executeString(null, script.evaluate().toString());
    }
}
