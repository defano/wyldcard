package com.defano.wyldcard.parts.model;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.properties.SimplePropertiesModel;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * An observer of changes to attributes in a {@link SimplePropertiesModel}.
 */
public interface PropertyChangeObserver {
    /**
     * Fired to indicate the value of an attribute was recently set, even when the new value is the same as the old.
     * <p>
     * This method will always be invoked on the dispatch thread, and, therefore, is not guaranteed to fire
     * synchronously with the attribute change.
     *
     * @param context  The execution context.
     * @param model    The property model that owns the property that is changing
     * @param property The name of the property (attribute) that changed.
     * @param oldValue The attribute's previous value
     * @param newValue The attribute's new value
     */
    void onPropertyChanged(ExecutionContext context, PropertiesModel model, String property, Value oldValue, Value newValue);
}
