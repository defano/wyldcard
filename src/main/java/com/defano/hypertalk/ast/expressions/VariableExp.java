/*
 * ExpVariable
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * VariableExp.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a variable expression in HyperTalk, for example: "myVariable"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import org.antlr.v4.runtime.ParserRuleContext;

public class VariableExp extends Expression {

    public final String identifier;
    
    public VariableExp(ParserRuleContext context, String identifier) {
        super(context);
        this.identifier = identifier;
    }
    
    public Value onEvaluate() {
        return ExecutionContext.getContext().get(identifier);
    }
}
