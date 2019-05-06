package com.defano.wyldcard.properties.value;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.properties.builder.PropertiesModelProvider;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * A value that delegates get/set operations to a property of the same name in a different {@link PropertiesModel}. The
 * read/write capability of the property is determined by the property and model to which it is delegated.
 */
public class DelegatedValue implements PropertyValue {

    private final transient PropertiesModelProvider provider;
    private final transient String propertyName;

    public DelegatedValue(String propertyName, PropertiesModelProvider provider) {
        this.propertyName = propertyName;
        this.provider = provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Value get(ExecutionContext context, PropertiesModel model) throws HtException {
        return this.provider.getPropertiesModel(context).get(context, propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(ExecutionContext context, Value v, PropertiesModel model) throws HtException {
        this.provider.getPropertiesModel(context).set(context, propertyName, v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DelegatedValue{" +
                "provider=" + provider +
                ", propertyName='" + propertyName + '\'' +
                '}';
    }
}
