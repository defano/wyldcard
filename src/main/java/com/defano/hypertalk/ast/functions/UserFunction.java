/*
 * UserFunction
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * UserFunction.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a user defined function definition
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ParameterList;
import com.defano.hypertalk.ast.statements.StatementList;

public class UserFunction {

    public final String name;
    public final ParameterList parameters;
    public final StatementList statements;
    
    public UserFunction (String function, ParameterList parameters, StatementList statements) {
        this.name = function;
        this.parameters = parameters;
        this.statements = statements;
    }
}
