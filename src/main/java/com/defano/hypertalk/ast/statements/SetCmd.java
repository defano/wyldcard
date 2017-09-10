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

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypertalk.ast.containers.Preposition;
import com.defano.hypertalk.ast.containers.PropertySpecifier;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.MenuPropertiesDelegate;

public class SetCmd extends Command {

    public final Expression expression;
    public final PropertySpecifier propertySpec;

    public SetCmd(PropertySpecifier propertySpec, Expression expression) {
        super("set");

        this.propertySpec = propertySpec;
        this.expression = expression;
    }
    
    public void onExecute () throws HtSemanticException {
        try {
            
            // Setting the property of HyperCard
            if (propertySpec.isGlobalPropertySpecifier()) {
                ExecutionContext.getContext().getGlobalProperties().setProperty(propertySpec.property, expression.evaluate());
            }

            else if (propertySpec.isMenuItemPropertySpecifier()) {
                MenuPropertiesDelegate.setProperty(propertySpec.property, expression.evaluate(), propertySpec.menuItem);
            }

            // Setting the property of a part
            else {
                ExecutionContext.getContext().set(propertySpec.property, propertySpec.partExp.evaluateAsSpecifier(), Preposition.INTO, null, expression.evaluate());
            }

        } catch (HtSemanticException e) {
            if (propertySpec.partExp != null) {
                throw (e);
            } else {
                // When all else fails, set the value of a variable container
                ExecutionContext.getContext().set(propertySpec.property, expression.evaluate());
            }
        }
    }
}
