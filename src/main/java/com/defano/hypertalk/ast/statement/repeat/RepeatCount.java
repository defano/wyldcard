package com.defano.hypertalk.ast.statement.repeat;

import com.defano.hypertalk.ast.expression.Expression;

public class RepeatCount extends RepeatSpecifier {
    public final Expression count;
    
    public RepeatCount (Expression count) {
        this.count = count;
    }
}
