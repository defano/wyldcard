package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.menu.HyperCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.forms.ScriptEditor;

public class DebugMenu extends HyperCardMenu {

    private final ScriptEditor editor;

    public DebugMenu(ScriptEditor editor) {
        super("Debug");
        this.editor = editor;

        MenuItemBuilder.ofCheckType()
                .named("Evaluate Expression...")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Toggle Breakpoint")
                .withAction(a -> editor.addBreakpoint())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Remove All Breakpoints")
                .withAction(a -> editor.clearBreakpoints())
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
                .withCheckmarkProvider(WindowManager.getInstance().getVariableWatcher().getWindowVisibleProvider())
                .withAction(a -> WindowManager.getInstance().getVariableWatcher().toggleVisible())
                .build(this);
    }
}
