/*
 * ExpProperty
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PropertyExp.java
 *
 * @author matt.defano@gmail.com
 * <p>
 * Encapsulation of a property, for example "visible of button id 10"
 */

package com.defano.hypertalk.ast.expressions;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PropertySpecifier;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.MenuPropertiesDelegate;

public class PropertyExp extends Expression {

    public final PropertySpecifier propertySpecifier;

    public PropertyExp(PropertySpecifier propertySpecifier) {
        this.propertySpecifier = propertySpecifier;
    }

    public Value evaluate() throws HtSemanticException {

        // Getting a HyperCard property
        if (propertySpecifier.isGlobalPropertySpecifier()) {
            return ExecutionContext.getContext().getGlobalProperties().getProperty(propertySpecifier.property);
        }

        // Getting a menu property
        else if (propertySpecifier.isMenuItemPropertySpecifier()) {
            return MenuPropertiesDelegate.getProperty(propertySpecifier.property, propertySpecifier.menuItem);
        }

        // Getting the property of a part
        else {
            return ExecutionContext.getContext().get(propertySpecifier.property, propertySpecifier.partExp.evaluateAsSpecifier());
        }

    }
}
