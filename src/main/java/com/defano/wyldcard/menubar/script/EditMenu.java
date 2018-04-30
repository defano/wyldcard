package com.defano.wyldcard.menubar.script;

import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.window.layouts.ScriptEditor;

public class EditMenu extends HyperCardMenu {

    public EditMenu(ScriptEditor editor) {
        super("Edit");

        MenuItemBuilder.ofDefaultType()
                .named("Undo")
                .withShortcut('Z')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.getEditor().getScriptField().undoLastAction())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Redo")
                .withShiftShortcut('Z')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.getEditor().getScriptField().redoLastAction())
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Cut")
                .withShortcut('X')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.getEditor().getScriptField().cut())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Copy")
                .withShortcut('C')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.getEditor().getScriptField().copy())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paste")
                .withShortcut('V')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.getEditor().getScriptField().paste())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Clear")
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.getEditor().getScriptField().replaceSelection(""))
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Select All")
                .withShortcut('A')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.getEditor().getScriptField().selectAll())
                .build(this);
    }
}
