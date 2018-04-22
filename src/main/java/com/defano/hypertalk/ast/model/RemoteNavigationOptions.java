package com.defano.hypertalk.ast.model;

public class RemoteNavigationOptions {

    public final boolean inNewWindow;
    public final boolean withoutDialog;

    public RemoteNavigationOptions(boolean inNewWindow, boolean withoutDialog) {
        this.inNewWindow = inNewWindow;
        this.withoutDialog = withoutDialog;
    }
}
