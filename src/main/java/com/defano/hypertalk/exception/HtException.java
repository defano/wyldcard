package com.defano.hypertalk.exception;

/**
 * A base class for all HyperTalk-related checked exceptions. HyperTalk exceptions may contain a "breadcrumb" that
 * identifies the part script and line number where the issue originated.
 */
public class HtException extends Exception {

    private Breadcrumb breadcrumb;

    public HtException(String message) {
        super(message);
    }

    public HtException(String message, HtException cause) {
        super(message, cause);
        this.breadcrumb = cause.breadcrumb;
    }

    protected HtException(HtException cause) {
        super(cause);
        this.breadcrumb = cause.getBreadcrumb();
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

    public String getStackTraceString() {
        if (getBreadcrumb() != null && getBreadcrumb().getContext() != null) {
            return getMessage() + "\n" + getBreadcrumb().getContext().getStackTrace();
        } else {
            return getMessage();
        }
    }
}
