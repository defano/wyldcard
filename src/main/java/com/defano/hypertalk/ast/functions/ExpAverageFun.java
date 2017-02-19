/*
 * ExpAverageFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:12 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpAverageFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation for the built-in function "average"
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;

public class ExpAverageFun extends ArgListFunction {

    public ExpAverageFun(ExpressionList argumentList) {
        super(argumentList);
    }

    public ExpAverageFun(Expression expression) {
        super(expression);
    }

    public Value evaluate () throws HtSemanticException {
        float sum = 0;
        List<Value> list = evaluateArgumentList();

        if (list.size() == 0) {
            return new Value(0);
        }
        
        for (Value item : list) {

            if (!item.isNumber()) {
                throw new HtSemanticException("Can't take the average of a non-numerical list");
            }
                
            sum += item.doubleValue();
        }
        
        return new Value(sum/list.size());
    }
}
