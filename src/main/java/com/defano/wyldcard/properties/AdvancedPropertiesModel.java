package com.defano.wyldcard.properties;

import com.defano.wyldcard.parts.model.PropertiesModel;

import java.util.ArrayList;

public class AdvancedPropertiesModel {

    ArrayList<Property> properties = new ArrayList<>();

    public void clear() {
        properties.clear();
    }

    public void newProperty(String property, PropertyValue value) {
        properties.add(new Property(new BasicPropertyName(property), value));
    }

    public void delegate(PropertiesModel delegate, String... delegatedProperties) {
        for (String thisProperty : delegatedProperties) {
            properties.add(new Property(new BasicPropertyName(thisProperty), new DelegatedPropertyValue(delegate)));
        }
    }

}
