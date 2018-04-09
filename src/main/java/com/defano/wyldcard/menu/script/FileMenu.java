package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.menu.HyperCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.forms.ScriptEditor;

public class FileMenu extends HyperCardMenu {

    public FileMenu(ScriptEditor editor) {
        super("File");

        MenuItemBuilder.ofDefaultType()
                .named("Close Script")
                .withShortcut('W')
                .withAction(e -> editor.close())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Save Script")
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.save())
                .withShortcut('S')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Revert to Saved")
                .withDisabledProvider(DebugContext.getInstance().getIsDebuggingProvider())
                .withAction(e -> editor.revertToSaved())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Script...")
                .withShortcut('P')
                .disabled()
                .build(this);

        if (!WindowManager.getInstance().isMacOs()) {

            addSeparator();

            MenuItemBuilder.ofDefaultType()
                    .named("Quit HyperCard")
                    .withAction(e -> WyldCard.getInstance().quit())
                    .withShortcut('Q')
                    .build(this);
        }

    }
}
