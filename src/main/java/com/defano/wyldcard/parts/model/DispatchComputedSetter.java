package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.aspect.RunOnDispatch;

/**
 * A computer property setter that always executes on the the Swing dispatch thread.
 */
public interface DispatchComputedSetter extends ComputedSetter {

    /**
     * Performs whatever action is required when setting the value of a computed property. In most cases, setting a
     * computed property results in setting one more other, non-computed properties. For example, setting a computed
     * rectangle value might result in setting non-computed top-left and bottom-right point attributes.
     *
     * Differs from {@link ComputedSetter} only in that this method will be run on the dispatch thread even if the
     * call to set the property is not.
     *
     * @param model        The {@link PropertiesModel} whose property is being set.
     * @param propertyName The name of the property which is to be set.
     * @param value        The requested value to be set; this method is responsible for transforming this value as
     *                     required.
     */
    @Override
    @RunOnDispatch
    void setComputedValue(PropertiesModel model, String propertyName, Value value);
}
