package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.menu.HyperCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.forms.ScriptEditor;
import com.defano.wyldcard.window.forms.TraceDelay;

public class DebugMenu extends HyperCardMenu {

    public DebugMenu(ScriptEditor editor) {
        super("Debug");

        MenuItemBuilder.ofCheckType()
                .named("Evaluate Expression...")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Toggle Breakpoint")
                .withAction(a -> editor.addBreakpoint())
                .withShortcut('B')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Remove All Breakpoints")
                .withAction(a -> editor.clearBreakpoints())
                .build(this);

        addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Trace")
                .withShortcut('T')
                .withAction(a -> DebugContext.getInstance().toggleTrace())
                .withCheckmarkProvider(DebugContext.getInstance().getIsTracingProvider())
                .withEnabledProvider(DebugContext.getInstance().getExecutionIsPausedProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Set Trace Delay...")
                .withEnabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(a -> WindowBuilder.make(new TraceDelay()).asModal().withTitle("Trace Delay").build())
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Run")
                .withShortcut('R')
                .withEnabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(a -> DebugContext.getInstance().resume())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Step Over")
                .withShortcut('O')
                .withAction(a -> DebugContext.getInstance().stepOver())
                .withEnabledProvider(DebugContext.getInstance().getExecutionIsPausedProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Step Out Of")
                .withShiftShortcut('O')
                .withAction(a -> DebugContext.getInstance().stepOut())
                .withEnabledProvider(DebugContext.getInstance().getExecutionIsPausedProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Step Into")
                .withShortcut('I')
                .withAction(a -> DebugContext.getInstance().stepInto())
                .withEnabledProvider(DebugContext.getInstance().getExecutionIsPausedProvider())
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
