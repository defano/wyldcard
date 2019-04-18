package com.defano.wyldcard.properties.builder;

import com.defano.wyldcard.properties.PropertiesModel;
import com.defano.wyldcard.properties.Property;
import com.defano.wyldcard.properties.value.ComputedReadWriteValue;
import com.defano.wyldcard.properties.value.ComputedValueGetter;
import com.defano.wyldcard.properties.value.ComputedValueSetter;

/**
 * Builds a {@link ComputedReadWriteValue} and automatically adds it to a {@link PropertiesModel} as soon as both a
 * getter and setter has been defined.
 */
public class ComputedReadWritePropertyBuilder {

    private final String[] propertyName;
    private final PropertiesModel propertiesModel;

    private ComputedValueSetter setter;
    private ComputedValueGetter getter;

    public ComputedReadWritePropertyBuilder(String[] propertyName, PropertiesModel model) {
        this.propertyName = propertyName;
        this.propertiesModel = model;
    }

    public ComputedReadWritePropertyBuilder withGetter(ComputedValueGetter getter) {
        this.getter = getter;

        if (this.setter != null) {
            propertiesModel.add(new Property(new ComputedReadWriteValue(this.getter, this.setter), propertyName));
        }

        return this;
    }

    public ComputedReadWritePropertyBuilder withSetter(ComputedValueSetter setter) {
        this.setter = setter;

        if (this.getter != null) {
            propertiesModel.add(new Property(new ComputedReadWriteValue(this.getter, this.setter), propertyName));
        }

        return this;
    }

}
