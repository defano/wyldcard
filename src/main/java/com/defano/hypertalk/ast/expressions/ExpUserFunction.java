/*
 * ExpUserFunction
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * ExpUserFunction.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of a user-defined function call, for example: "myfunction(arg)"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.PartException;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtSemanticException;

public class ExpUserFunction extends Expression {

    public final String function;
    public final ExpressionList arguments;

    public ExpUserFunction (String function, ExpressionList arguments) {
        this.function = function;
        this.arguments = arguments;
    }
    
    public Value evaluate () throws HtSemanticException {
        
        try {
            PartSpecifier ps = GlobalContext.getContext().getMe();
            Part part = GlobalContext.getContext().get(ps);
            
            arguments.evaluate();
            return part.executeUserFunction(function, arguments);
        } catch (PartException e) {
            throw new HtSemanticException(e.getMessage());
        }                        
    }
}
