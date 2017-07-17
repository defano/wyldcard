/*
 * ExpPart
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartExp.java
 * @author matt.defano@gmail.com
 * 
 * Abstract superclass for part expressions
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public abstract class PartExp extends Expression {

    public abstract PartSpecifier evaluateAsSpecifier () throws HtSemanticException;
}
