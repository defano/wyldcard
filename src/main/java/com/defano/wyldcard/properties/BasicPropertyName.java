package com.defano.wyldcard.properties;

public class BasicPropertyName implements PropertyName {

    private final String propertyName;

    public BasicPropertyName(String name) {
        this.propertyName = name;
    }

    public String getName() {
        return propertyName;
    }

    @Override
    public boolean matches(String propertyName) {
        return this.propertyName.equalsIgnoreCase(propertyName);
    }
}
