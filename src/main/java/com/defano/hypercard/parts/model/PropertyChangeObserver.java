/*
 * PropertyChangeObserver
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PropertyChangeObserver.java
 * @author matt.defano@gmail.com
 * 
 * Interface allowing an object to receive notification when a part's model
 * have changed.
 */

package com.defano.hypercard.parts.model;

import com.defano.hypertalk.ast.common.Value;

/**
 * An observer of changes to attributes in a {@link PropertiesModel}.
 */
public interface PropertyChangeObserver {
    /**
     * Fired to indicate the value of an attribute was changed.
     *
     * @param property The name of the property (attribute) that changed.
     * @param oldValue The attribute's previous value
     * @param newValue The attribute's new value
     */
    void onPropertyChanged(String property, Value oldValue, Value newValue);
}
