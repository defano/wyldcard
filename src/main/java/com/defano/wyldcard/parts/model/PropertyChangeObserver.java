package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * An observer of changes to attributes in a {@link PropertiesModel}.
 */
public interface PropertyChangeObserver {
    /**
     * Fired to indicate the value of an attribute was recently changed. Note that this method will always be invoked on
     * the dispatch thread, and, therefore, is not guaranteed to fire synchronously with the attribute change.
     * Note that this method will fire even when oldValue is equal to newValue.
     *
     * Use {@link PropertyWillChangeObserver} for a synchronous notification of a property change.
     *
     * @param context The execution context.
     * @param property The name of the property (attribute) that changed.
     * @param oldValue The attribute's previous value
     * @param newValue The attribute's new value
     */
    void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue);
}
