package com.defano.wyldcard.menu.script;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.debug.DebugContext;
import com.defano.wyldcard.menu.WyldCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.layout.ScriptEditor;

public class FileMenu extends WyldCardMenu {

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

        if (!WyldCard.getInstance().getWindowManager().isMacOsTheme()) {

            addSeparator();

            MenuItemBuilder.ofDefaultType()
                    .named("Quit HyperCard")
                    .withAction(e -> WyldCard.getInstance().getStackManager().closeAllStacks(new ExecutionContext()))
                    .withShortcut('Q')
                    .build(this);
        }

    }
}
