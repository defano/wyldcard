package com.defano.hypercard.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ArtVandelay;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.runtime.print.PrintCardAction;
import com.defano.hypercard.runtime.print.PrintStackAction;
import com.defano.hypercard.window.WindowManager;

import java.util.Objects;

/**
 * The HyperCard File menu.
 */
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
                .named("Save Stack")
                .withAction(e -> HyperCard.getInstance().getStack().save(HyperCard.getInstance().getSavedStackFile()))
                .withDisabledProvider(HyperCard.getInstance().getSavedStackFileProvider().map(op -> !op.isPresent()))
                .withShortcut('S')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Save Stack As...")
                .withAction(e -> HyperCard.getInstance().getStack().saveAs())
                .withShiftShortcut('S')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Import Paint...")
                .withAction(e -> ArtVandelay.importPaint())
                .withDisabledProvider(ToolsContext.getInstance().getToolModeProvider().map(m -> m != ToolMode.PAINT))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Export Paint...")
                .withAction(e -> ArtVandelay.exportPaint())
                .withDisabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Objects::isNull))
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
                    .withAction(e -> HyperCard.getInstance().quit())
                    .withShortcut('Q')
                    .build(this);
        }
    }

    public void reset() {
        instance = new FileMenu();
    }
}
