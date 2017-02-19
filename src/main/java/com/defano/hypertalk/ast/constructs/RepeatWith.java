/*
 * RepeatWith
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * RepeatWith.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "repeat with x = y to z" construct
 */

package com.defano.hypertalk.ast.constructs;

public class RepeatWith extends RepeatSpecifier {

    public final String symbol;
    public final RepeatRange range;
    
    public RepeatWith (String symbol, RepeatRange range) {
        this.symbol = symbol;
        this.range = range;
    }
}
