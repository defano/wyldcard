/*
 * ExpNumberOfFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpNumberOfFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the number of"
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ChunkType;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpNumberOfFun extends Expression {

    public final ChunkType itemtype;
    public final Expression expression;
    
    public ExpNumberOfFun(ChunkType itemtype, Expression expression) {
        this.itemtype = itemtype;
        this.expression = expression;
    }
    
    public Value evaluate () throws HtSemanticException {
        switch (itemtype) {
        case CHAR: return new Value(expression.evaluate().charCount());
        case WORD: return new Value(expression.evaluate().wordCount());
        case LINE: return new Value(expression.evaluate().lineCount());
        case ITEM: return new Value(expression.evaluate().itemCount());
        default: throw new RuntimeException("Unhandeled coutable item type");
        }
    }
}
