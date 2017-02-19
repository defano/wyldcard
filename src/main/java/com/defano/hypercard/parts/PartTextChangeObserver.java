/*
 * PartTextChangeObserver
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts;

public interface PartTextChangeObserver {
    void onTextChanged(String newText);
}
