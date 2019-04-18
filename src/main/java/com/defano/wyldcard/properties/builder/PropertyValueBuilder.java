package com.defano.wyldcard.properties.builder;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.properties.Property;
import com.defano.wyldcard.properties.value.*;

import java.util.Arrays;

/**
 * Builds the value portion of a {@link Property} and adds it to a {@link PropertiesModel}.
 */
public class PropertyValueBuilder {

    private final String[] propertyNames;   // Name(s) that this property is identified by.
    private final PropertiesModel model;    // Model that owns this property

    public PropertyValueBuilder(PropertiesModel model, String... propertyNames) {
        this.model = model;
        this.propertyNames = propertyNames;
    }

    /**
     * Creates a {@link Property} whose value mirrors a property of the same name contained in a different
     * {@link PropertiesModel}.
     *
     * @param provider Provides the delegate model that is used to provide the value of this property.
     */
    public void byDelegatingToModel(PropertiesModelProvider provider) {
        Arrays.stream(propertyNames).map(propertyName -> new Property(new DelegatedValue(propertyName, provider), propertyName)).forEach(model::add);
    }

    /**
     * Defines a {@link Property} with an empty, writable value.
     */
    public void asValue() {
        model.add(new Property(new BasicValue(new Value()), propertyNames));
    }

    /**
     * Defines a {@link Property} whose writable value is initialized with the given value.
     *
     * @param v The initial value of the property.
     */
    public void asValue(Object v) {
        model.add(new Property(new BasicValue(new Value(v)), propertyNames));
    }

    /**
     * Defines a read-only {@link Property} set equal to the provided value.
     *
     * @param v The value of the property.
     */
    public void asConstant(Object v) {
        model.add(new Property(new ConstantValue(new Value(v)), propertyNames));
    }

    /**
     * Defines a {@link Property} whose value is synthesized.
     *
     * @return A {@link ComputedReadWritePropertyBuilder} for building getter and setter.
     */
    public ComputedReadWritePropertyBuilder asComputedValue() {
        return new ComputedReadWritePropertyBuilder(propertyNames, model);
    }

    /**
     * Defines this property to be an alias of another property in the same {@link PropertiesModel}.
     *
     * @param propertyName The name of the property that this property delegates to.
     */
    public void asAliasOf(String propertyName) {
        model.findProperty(propertyName).addAliases(propertyNames);
    }

    /**
     * Deifines a read-only {@link Property} whose value is synthesized.
     *
     * @param getter The method invoked when the value of this property is requested.
     */
    public void asComputedReadOnlyValue(ComputedValueGetter getter) {
        model.add(new Property(new ComputedReadOnlyValue(getter), propertyNames));
    }

}
