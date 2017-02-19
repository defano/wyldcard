/*
 * Expression
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * Expression.java
 * @author matt.defano@gmail.com
 * 
 * Abstract superclass of all expression types
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;


public abstract class Expression {

    public abstract Value evaluate() throws HtSemanticException;
}
