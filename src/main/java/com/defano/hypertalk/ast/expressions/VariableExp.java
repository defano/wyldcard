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

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.common.Value;

public class VariableExp extends Expression {

    public final String identifier;
    
    public VariableExp(String identifier) {
        this.identifier = identifier;
    }
    
    public Value evaluate () {
        return GlobalContext.getContext().get(identifier);
    }
}
