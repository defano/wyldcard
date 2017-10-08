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

import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class PartExp extends Expression {

    public PartExp(ParserRuleContext context) {
        super(context);
    }

    public abstract PartSpecifier evaluateAsSpecifier () throws HtException;
}
