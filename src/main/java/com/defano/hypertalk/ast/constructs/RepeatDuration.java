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
