/*
 * PropertyPermissionException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:12 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * PropertyPermissionException.java
 * @author matt.defano@gmail.com
 * 
 * Thrown when attempting to set a read-only property (such as id) on a part
 */

package com.defano.hypertalk.exception;

/**
 * Indicates an attempt to write a property that is read-only.
 */
public class PropertyPermissionException extends HtSemanticException {

    public PropertyPermissionException (String message) {
        super(message);
    }
}
