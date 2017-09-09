/*
 * ExpressionList
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpressionList.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Encapsulation of a function's argument list. Arguments in the
 * list are not evaluated until the function is called.
 */

package com.defano.hypertalk.ast.common;

import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.ArrayList;
import java.util.List;

public class ExpressionList {

    private final List<Expression> list = new ArrayList<>();

    public ExpressionList() {
    }

    public ExpressionList(String... boundValues) {
        for (String thisValue : boundValues) {
            addArgument(new LiteralExp(thisValue));
        }
    }

    public ExpressionList(Expression expr) {
        list.add(expr);
    }

    public ExpressionList addArgument(Expression expr) {
        list.add(expr);
        return this;
    }

    public List<Value> divingEvaluate() throws HtSemanticException {
        List<Value> evaluatedList = new ArrayList<>();

        for (Expression expr : list) {
            evaluatedList.addAll(expr.evaluate().getItems());
        }

        return evaluatedList;
    }

    public List<Value> evaluate() throws HtSemanticException {
        List<Value> evaluatedList = new ArrayList<>();

        for (Expression expr : list) {
            evaluatedList.add(expr.evaluate());
        }

        return evaluatedList;
    }

    public int getArgumentCount() {
        return list.size();
    }
}
