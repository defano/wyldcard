/*
 * ExpUserFunction
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * UserFunctionExp.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Encapsulation of a user-defined function call, for example: "myfunction(arg)"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class UserFunctionExp extends Expression {

    public final String function;
    public final ExpressionList arguments;

    public UserFunctionExp(String function, ExpressionList arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public Value evaluate() throws HtSemanticException {

        if (!ExecutionContext.getContext().hasMe()) {
            throw new HtSemanticException("Cannot invoke user-defined functions here.");
        }

        PartSpecifier ps = ExecutionContext.getContext().getMe();
        PartModel part = ExecutionContext.getContext().get(ps);

        return part.invokeFunction(function, arguments);
    }
}
