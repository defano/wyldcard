package com.defano.wyldcard.property.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.property.PropertiesModel;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface ComputedValueSetter {
    /**
     * Performs whatever action is required when setting the value of a computed property. In most cases, setting a
     * computed property results in setting one more other, non-computed properties. For example, setting a computed
     * rectangle value might result in setting non-computed top-left and bottom-right point attributes.
     *
     * @param context      The execution context.
     * @param model        The {@link PropertiesModel} whose property is being set.
     * @param value        The requested value to be set; this method is responsible for transforming this value as
     *                     required.
     * @throws HtSemanticException Thrown to indicate the property cannot accept the given/computed value.
     */
    void setComputedValue(ExecutionContext context, PropertiesModel model, Value value) throws HtSemanticException;
}
