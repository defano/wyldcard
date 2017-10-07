/*
 * Expression
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * Expression.java
 * @author matt.defano@gmail.com
 * 
 * Abstract superclass of all expression types
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.exception.HtSyntaxException;
import org.antlr.v4.runtime.ParserRuleContext;


public abstract class Expression extends ASTNode {

    public Expression(ParserRuleContext context) {
        super(context);
    }

    protected abstract Value onEvaluate() throws HtSemanticException;

    public Value evaluate() throws HtSemanticException {
        try {
            return onEvaluate();
        } catch (HtSemanticException e) {
            // TODO: Wrap exception with parse context
            throw e;
        }
    }
}
