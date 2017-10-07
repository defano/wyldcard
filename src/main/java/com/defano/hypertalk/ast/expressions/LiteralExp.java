/*
 * ExpLiteral
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * LiteralExp.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a literal value in HyperTalk, for example: "Hello world"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.common.Value;
import org.antlr.v4.runtime.ParserRuleContext;

public class LiteralExp extends Expression {

    public final String literal;

    public LiteralExp(ParserRuleContext context, Object literal) {
        super(context);
        this.literal = String.valueOf(literal);
    }
    
    public Value onEvaluate() {
        return new Value(literal);
    }
}
