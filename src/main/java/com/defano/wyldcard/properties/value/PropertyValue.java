package com.defano.wyldcard.properties.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * Represents the value of a property, which may be computed on get, on set, or after get.
 */
public interface PropertyValue {

    /**
     * Gets the value of this property.
     *
     * @param context The execution context
     * @param model   The PropertiesModel that owns the property.
     * @return The value of this property.
     * @throws HtException Thrown if an error occurs getting or calculating the value of the property.
     */
    Value get(ExecutionContext context, PropertiesModel model) throws HtException;

    /**
     * Sets the value of this property.
     *
     * @param context The execution context
     * @param v       The Value that this property should take
     * @param model   The PropertiesModel that owns the property.
     * @throws HtException Thrown if an error occurs setting the value of the property.
     */
    void set(ExecutionContext context, Value v, PropertiesModel model) throws HtException;

    /**
     * Specifies a transform that will be executed after a call to {@link #get(ExecutionContext, PropertiesModel)} and
     * which can be used to modify the value returned to the caller getting the value.
     *
     * @param transform The transform to apply.
     */
    default void applyOnGetTransform(ValueTransformer transform) {
        throw new UnsupportedOperationException("This kind of property value does not support transforms.");
    }

}
