package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.menu.HyperCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.window.WindowManager;

public class DebugMenu extends HyperCardMenu {
    public DebugMenu() {
        super("Debug");

        MenuItemBuilder.ofCheckType()
                .named("Evaluate Expression...")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Toggle Breakpoint")
                .disabled()
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Remove All Breakpoints")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Run")
                .withShortcut('R')
                .disabled()
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Step Over")
                .withShortcut('S')
                .disabled()
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Step Into")
                .withShortcut('I')
                .disabled()
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Step Out Of")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Message Watcher")
                .withCheckmarkProvider(WindowManager.getInstance().getMessageWatcher().getWindowVisibleProvider())
                .withAction(a -> WindowManager.getInstance().getMessageWatcher().toggleVisible())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Variable Watcher")
                .disabled()
                .build(this);
    }
}
