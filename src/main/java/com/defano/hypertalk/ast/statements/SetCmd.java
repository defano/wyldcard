/*
 * StatSetCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * SetCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "set" command (for mutating a property)
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.containers.ContainerVariable;
import com.defano.hypertalk.ast.containers.Preposition;
import com.defano.hypertalk.ast.containers.PropertySpecifier;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;

public class SetCmd extends Statement {

    public final Expression expression;
    public final PropertySpecifier propertySpec;

    public SetCmd(PropertySpecifier propertySpec, Expression expression) {
        this.propertySpec = propertySpec;
        this.expression = expression;
    }
    
    public void execute () throws HtSemanticException {
        try {
            
            // Setting the property of HyperCard
            if (propertySpec.isGlobalPropertySpecifier()) {
                GlobalContext.getContext().getGlobalProperties().setProperty(propertySpec.property, expression.evaluate());
            }

            // Setting the property of a part
            else {
                GlobalContext.getContext().set(propertySpec.property, propertySpec.partExp.evaluateAsSpecifier(), Preposition.INTO, null, expression.evaluate());
            }

        } catch (Exception e) {

            if (propertySpec.partExp != null) {
                throw new HtSemanticException("Cannot set the '" + propertySpec.property + "' of this part.", e);
            } else {
                // When all else fails, set the value of a container
                GlobalContext.getContext().put(expression.evaluate(), Preposition.INTO, new ContainerVariable(propertySpec.property));
            }
        }
    }
}
