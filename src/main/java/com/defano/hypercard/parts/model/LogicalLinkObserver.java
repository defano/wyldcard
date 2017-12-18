package com.defano.hypercard.parts.model;

import com.defano.hypertalk.ast.model.Value;

/**
 * Logically links two logical (boolean-valued) properties together. That is, when one property changes state, the other
 * property should also change. This is useful for managing mutually exclusive properties in a model.
 */
public class LogicalLinkObserver implements PropertyChangeObserver {

    private final String whenProperty;
    private final boolean whenState;
    private final String thenProperty;
    private final boolean thenState;

    public static LogicalLinkObserver clearOnSet(String whenClearProperty, String thenSetProperty) {
        return new LogicalLinkObserver(whenClearProperty, false, thenSetProperty, true);
    }

    public static LogicalLinkObserver setOnClear(String whenSetProperty, String thenClearProperty) {
        return new LogicalLinkObserver(whenSetProperty, false, thenClearProperty, true);
    }

    public static LogicalLinkObserver setOnSet(String whenSetProperty, String thenSetProperty) {
        return new LogicalLinkObserver(whenSetProperty, true, thenSetProperty, true);
    }

    public static LogicalLinkObserver clearOnClear(String whenClearProperty, String thenClearProperty) {
        return new LogicalLinkObserver(whenClearProperty, false, thenClearProperty, false);
    }

    private LogicalLinkObserver(String whenProperty, boolean whenState, String thenProperty, boolean thenState) {
        this.whenProperty = whenProperty;
        this.whenState = whenState;
        this.thenProperty = thenProperty;
        this.thenState = thenState;
    }

    @Override
    public void onPropertyChanged(PropertiesModel model, String property, Value oldValue, Value newValue) {
        if (property.equalsIgnoreCase(whenProperty) && newValue.booleanValue() == whenState) {
            model.setKnownProperty(thenProperty, new Value(thenState));
        }
    }
}
