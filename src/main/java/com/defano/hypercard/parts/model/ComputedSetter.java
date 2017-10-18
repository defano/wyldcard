package com.defano.hypercard.parts.model;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSemanticException;

/**
 * Represents a writable attribute whose value is computationally derived.
 */
public interface ComputedSetter {

    /**
     * Performs whatever action is required when setting the value of a computed property. In most cases, setting a
     * computed property results in setting one more other, non-computed properties. For example, setting a computed
     * rectangle value might result in setting non-computed top-left and bottom-right point attributes.
     *
     * @param model        The {@link PropertiesModel} whose property is being set.
     * @param propertyName The name of the property which is to be set.
     * @param value        The requested value to be set; this method is responsible for transforming this value as
     *                     required.
     * @throws HtSemanticException Thrown to indicate the property cannot accept the given/computed value.
     */
    void setComputedValue(PropertiesModel model, String propertyName, Value value) throws HtSemanticException;
}
