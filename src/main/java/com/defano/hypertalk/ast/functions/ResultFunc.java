/*
 * ExpResultFun
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.functions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.common.Value;

public class ResultFunc extends Expression {

    public ResultFunc() {}
    
    public Value evaluate () {
        return ExecutionContext.getContext().getResult();
    }
}
