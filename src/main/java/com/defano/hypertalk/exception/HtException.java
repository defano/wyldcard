/*
 * HtException
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.exception;

import com.defano.hypercard.runtime.Breadcrumb;

/**
 * A base class for all HyperTalk-related checked exceptions.
 */
public class HtException extends Exception {

    private Breadcrumb breadcrumb;

    public HtException(HtException cause) {
        this(getRootCause(cause).getMessage(), cause);
    }

    public HtException(String message) {
        super(message);
    }

    public HtException(String message, HtException cause) {
        super(message, cause);
        this.breadcrumb = cause.breadcrumb;
    }

    public static HtException getRootCause(HtException cause) {
        if (cause.getCause() == null) {
            return cause;
        } else {
            return getRootCause((HtException) cause.getCause());
        }
    }

    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }

    public void setBreadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
    }
}
