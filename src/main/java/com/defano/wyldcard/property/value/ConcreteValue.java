package com.defano.wyldcard.property.value;

import com.defano.hypertalk.ast.model.Value;

/**
 * A {@link PropertyValue} backed by a concrete {@link Value} object.
 */
public interface ConcreteValue extends PropertyValue {

    /**
     * Gets the backing {@link Value} associated with this {@link PropertyValue}, without invoking any synthesized
     * getter, setter or transform.
     *
     * @return The raw, backing value of this {@link PropertyValue}.
     */
    Value rawValue();

}
