package com.defano.wyldcard.properties;

public class Property {

    private final PropertyName name;
    private final PropertyValue value;

    public Property(PropertyName name, PropertyValue value) {
        this.name = name;
        this.value = value;
    }
}
