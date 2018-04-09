package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.menu.HyperCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.forms.FindDialog;
import com.defano.wyldcard.window.forms.ReplaceDialog;
import com.defano.wyldcard.window.forms.ScriptEditor;

public class ScriptMenu extends HyperCardMenu {

    public ScriptMenu(ScriptEditor editor) {
        super("Script");

        MenuItemBuilder.ofDefaultType()
                .named("Find...")
                .withShortcut('F')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> WindowBuilder.make(new FindDialog())
                        .withTitle("Find")
                        .withModel(editor)
                        .withLocationCenteredOver(editor)
                        .asModal()
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Find Again")
                .withShortcut('G')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.find())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Enter 'Find' String")
                .withShortcut('E')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.makeSelectionFindText())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Find Selection")
                .withShortcut('H')
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.findSelection())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Scroll to Selection")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Replace...")
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> WindowBuilder.make(new ReplaceDialog())
                        .withTitle("Replace")
                        .withModel(editor)
                        .asModal()
                        .withLocationCenteredOver(editor)
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Replace Again")
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.replace())
                .withShortcut('T')
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Comment")
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withShortcut('-')
                .withAction(e -> editor.comment())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Uncomment")
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withShortcut('=')
                .withAction(e -> editor.uncomment())
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Check Syntax")
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withShortcut('K')
                .withAction(e -> editor.checkSyntax())
                .build(this);
    }

}
