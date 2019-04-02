package com.defano.hypertalk.exception;

import com.defano.wyldcard.runtime.Breadcrumb;

/**
 * A base class for all HyperTalk-related checked exceptions. HyperTalk exceptions may contain a "breadcrumb" that
 * identifies the part script and line number where the issue originated.
 */
public class HtException extends Exception {

    private Breadcrumb breadcrumb;

    public HtException(HtException cause) {
        this(getRootCause(cause).getMessage(), getRootCause(cause));
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
