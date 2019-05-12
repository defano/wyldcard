package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.menu.WyldCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.layout.ScriptEditor;
import com.defano.wyldcard.window.layout.TraceDelay;

public class DebugMenu extends WyldCardMenu {

    public DebugMenu(ScriptEditor editor) {
        super("Debug");

        MenuItemBuilder.ofCheckType()
                .named("Evaluate Expression...")
                .withEnabledProvider(DebugContext.getInstance().getExecutionIsPausedProvider())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getExpressionEvaluator().getWindowVisibleProvider())
                .withAction(a -> WyldCard.getInstance().getWindowManager().getExpressionEvaluator().toggleVisible())
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
                .withAction(a -> new WindowBuilder<>(new TraceDelay()).asModal().withTitle("Trace Delay").build())
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Run")
                .withShortcut('R')
                .withEnabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(a -> DebugContext.getInstance().resume())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Abort")
                .withShortcut('A')
                .withEnabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(a -> DebugContext.getInstance().abort())
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
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getMessageWatcher().getWindowVisibleProvider())
                .withAction(a -> WyldCard.getInstance().getWindowManager().getMessageWatcher().toggleVisible())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Variable Watcher")
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getVariableWatcher().getWindowVisibleProvider())
                .withAction(a -> WyldCard.getInstance().getWindowManager().getVariableWatcher().toggleVisible())
                .build(this);
    }
}
