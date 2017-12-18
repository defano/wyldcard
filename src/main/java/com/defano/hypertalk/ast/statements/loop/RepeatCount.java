package com.defano.hypertalk.ast.statements.loop;

import com.defano.hypertalk.ast.expressions.Expression;

public class RepeatCount extends RepeatSpecifier {
    public final Expression count;
    
    public RepeatCount (Expression count) {
        this.count = count;
    }
}
