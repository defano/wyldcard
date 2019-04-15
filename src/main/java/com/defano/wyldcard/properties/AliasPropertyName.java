package com.defano.wyldcard.properties;

import java.util.ArrayList;

public class AliasPropertyName implements PropertyName {

    private final ArrayList<String> propertyNames = new ArrayList<>();

    public AliasPropertyName(String... propertyNames) {
        for (String propertyName : propertyNames) {
            this.propertyNames.add(propertyName.toLowerCase());
        }
    }

    @Override
    public String getName() {
        return propertyNames.get(0);
    }

    @Override
    public boolean matches(String propertyName) {
        return this.propertyNames.contains(propertyName.toLowerCase());
    }
}
