/*
 * PartException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.parts;

import com.defano.hypertalk.exception.HtException;

/**
 * Represents an error that occurs when creating, removing or modifying a part.
 */
public class PartException extends HtException {
    public PartException(String message) {
        super(message);
    }
}
