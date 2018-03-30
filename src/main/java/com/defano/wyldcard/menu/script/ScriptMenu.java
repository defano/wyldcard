package com.defano.wyldcard.menu.script;

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
                .withAction(e -> editor.find())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Enter 'Find' String")
                .withShortcut('E')
                .withAction(e -> editor.makeSelectionFindText())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Find Selection")
                .withShortcut('H')
                .withAction(e -> editor.findSelection())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Scroll to Selection")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Replace...")
                .withShortcut('R')
                .withAction(e -> WindowBuilder.make(new ReplaceDialog())
                        .withTitle("Replace")
                        .withModel(editor)
                        .asModal()
                        .withLocationCenteredOver(editor)
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Replace Again")
                .withAction(e -> editor.replace())
                .withShortcut('T')
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Comment")
                .withShortcut('-')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Uncomment")
                .withShortcut('=')
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Check Syntax")
                .withShortcut('K')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Set Checkpoint")
                .withShortcut('D')
                .disabled()
                .build(this);
    }

}
