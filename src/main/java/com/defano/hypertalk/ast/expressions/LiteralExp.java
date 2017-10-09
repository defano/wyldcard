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

    public static LiteralExp ofCardinal(ParserRuleContext context, String cardinal) {
        switch (cardinal.toLowerCase()) {
            case "one": return new LiteralExp(context, 1);
            case "two": return new LiteralExp(context, 2);
            case "three": return new LiteralExp(context, 3);
            case "four": return new LiteralExp(context, 4);
            case "five": return new LiteralExp(context, 5);
            case "six": return new LiteralExp(context, 6);
            case "seven": return new LiteralExp(context, 7);
            case "eight": return new LiteralExp(context, 8);
            case "nine": return new LiteralExp(context, 9);
            case "ten": return new LiteralExp(context, 10);

            default: throw new IllegalArgumentException("Bug! Unimplemented cardinal: " + cardinal);
        }
    }
}
