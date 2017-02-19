/*
 * RepeatRange
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * RepeatRange.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "x to y" and "x downto y" constructs (used within the
 * RepeatWith construct). 
 */

package com.defano.hypertalk.ast.constructs;

import com.defano.hypertalk.ast.expressions.Expression;

public class RepeatRange extends RepeatSpecifier {

    public static final boolean POLARITY_UPTO = true;
    public static final boolean POLARITY_DOWNTO = false;
    
    public final boolean polarity;
    public final Expression from;
    public final Expression to;
    
    public RepeatRange(boolean polarity, Expression from, Expression to) {
        this.polarity = polarity;
        this.from = from;
        this.to = to;
    }
}
