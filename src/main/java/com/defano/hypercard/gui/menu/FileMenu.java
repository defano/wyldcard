/*
 * FileMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.gui.util.ArtVandelay;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.hypercard.runtime.print.PrintCardAction;
import com.defano.hypercard.runtime.print.PrintStackAction;
import com.defano.jmonet.model.ImmutableProvider;

import java.util.Objects;

public class FileMenu extends HyperCardMenu {

    public static FileMenu instance = new FileMenu();

    private FileMenu() {
        super("File");

        MenuItemBuilder.ofDefaultType()
                .named("New Stack...")
                .withAction(e -> HyperCard.getInstance().openStack(StackModel.newStackModel("Untitled")))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Open Stack...")
                .withAction(e -> HyperCard.getInstance().getStack().open())
                .withShortcut('O')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Close Stack")
                .withShortcut('W')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Save Stack...")
                .withAction(e -> HyperCard.getInstance().getStack().save())
                .withShortcut('S')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Import Paint...")
                .withAction(e -> ArtVandelay.importPaint())
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), m -> m != ToolMode.PAINT))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Export Paint...")
                .withAction(e -> ArtVandelay.exportPaint())
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Compact Stack")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Protect Stack...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Stack...")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Page Setup...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Field...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Card...")
                .withShortcut('P')
                .withAction(new PrintCardAction())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Stack...")
                .withShiftShortcut('P')
                .withAction(new PrintStackAction())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Report...")
                .disabled()
                .build(this);

        if (!WindowManager.isMacOs()) {

            this.addSeparator();

            MenuItemBuilder.ofDefaultType()
                    .named("Quit HyperCard")
                    .withAction(e -> System.exit(0))
                    .withShortcut('Q')
                    .build(this);
        }
    }

    public void reset() {
        instance = new FileMenu();
    }
}
