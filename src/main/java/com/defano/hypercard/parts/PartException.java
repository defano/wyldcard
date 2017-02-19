/*
 * PartException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PartException.java
 * @author matt.defano@gmail.com
 * 
 * Exception to be thrown when a request is made for a part that doesn't exist
 * or otherwise cannot handle the request.
 */

package com.defano.hypercard.parts;

import com.defano.hypertalk.exception.HtException;

public class PartException extends HtException {
    public PartException(String message) {
        super(message);
    }
}
