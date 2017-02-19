/*
 * ExpResultFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpResultsFun.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the built-in function "the result"
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.common.Value;

public class ExpResultFun extends Expression {

    public ExpResultFun () {}
    
    public Value evaluate () {
        return GlobalContext.getContext().getIt();
    }
}
