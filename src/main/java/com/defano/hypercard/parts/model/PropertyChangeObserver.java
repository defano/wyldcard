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

public interface PropertyChangeObserver {
    void onPropertyChanged(String property, Value oldValue, Value newValue);
}
