package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.menu.HyperCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.window.forms.ScriptEditor;

public class EditMenu extends HyperCardMenu {

    public EditMenu(ScriptEditor editor) {
        super("Edit");

        MenuItemBuilder.ofDefaultType()
                .named("Undo")
                .withShortcut('Z')
                .withAction(e -> editor.getEditor().getScriptField().undoLastAction())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Redo")
                .withShiftShortcut('Z')
                .withAction(e -> editor.getEditor().getScriptField().redoLastAction())
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Cut")
                .withShortcut('X')
                .withAction(e -> editor.getEditor().getScriptField().cut())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Copy")
                .withShortcut('C')
                .withAction(e -> editor.getEditor().getScriptField().copy())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paste")
                .withShortcut('V')
                .withAction(e -> editor.getEditor().getScriptField().paste())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Clear")
                .withAction(e -> editor.getEditor().getScriptField().replaceSelection(""))
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Select All")
                .withShortcut('A')
                .withAction(e -> editor.getEditor().getScriptField().selectAll())
                .build(this);
    }
}
