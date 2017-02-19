/*
 * RepeatCount
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * RepeatCount.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "repeat for x times" construct
 */

package com.defano.hypertalk.ast.constructs;

import com.defano.hypertalk.ast.expressions.Expression;

public class RepeatCount extends RepeatSpecifier {
    public final Expression count;
    
    public RepeatCount (Expression count) {
        this.count = count;
    }
}
