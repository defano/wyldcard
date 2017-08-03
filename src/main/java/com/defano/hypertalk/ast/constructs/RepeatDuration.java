/*
 * RepeatDuration
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * RepeatDuration.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "repeat while..." and "repeat until..." constructs
 */

package com.defano.hypertalk.ast.constructs;

import com.defano.hypertalk.ast.expressions.Expression;

public class RepeatDuration extends RepeatSpecifier {

    public static final boolean POLARITY_WHILE = true;
    public static final boolean POLARITY_UNTIL = false;
    
    public final boolean polarity;
    public final Expression condition;
    
    public RepeatDuration (boolean polarity, Expression condition) {
        this.polarity = polarity;
        this.condition = condition;
    }    
}
