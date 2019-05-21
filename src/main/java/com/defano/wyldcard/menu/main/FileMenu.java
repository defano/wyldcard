package com.defano.wyldcard.menu.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.importer.StackImporter;
import com.defano.wyldcard.menu.WyldCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.paint.ArtVandelay;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.runtime.print.PrintCardAction;
import com.defano.wyldcard.runtime.print.PrintStackAction;

import java.util.Optional;

/**
 * The HyperCard File menu.
 */
public class FileMenu extends WyldCardMenu {

    public static FileMenu instance = new FileMenu();

    private FileMenu() {
        super("File");

        MenuItemBuilder.ofDefaultType()
                .named("New Stack")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().newStack(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Open Stack...")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().findAndOpenStack(new ExecutionContext(), false, "Open Stack"))
                .withShortcut('O')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Open New Window...")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().findAndOpenStack(new ExecutionContext(), true, "Open Stack in New Window"))
                .withShiftShortcut('O')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Close Stack")
                .withShortcut('W')
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().closeStack(new ExecutionContext(), null))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Save Stack")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().saveStack(new ExecutionContext(), null))
                .withEnabledProvider(WyldCard.getInstance().getStackManager().getSavedStackFileProvider().map(Optional::isPresent))
                .withShortcut('S')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Save Stack As...")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().saveStackAs(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel()))
                .withShiftShortcut('S')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Import HyperCard Stack...")
                .withShiftShortcut('I')
                .withDoMenuAction(e -> StackImporter.importStack(new ExecutionContext()))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Import Paint...")
                .withDoMenuAction(e -> ArtVandelay.importPaint())
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(m -> m != ToolMode.PAINT))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Export Paint...")
                .withDoMenuAction(e -> ArtVandelay.exportPaint())
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(m -> m != ToolMode.PAINT))
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
                .withDoMenuAction(new PrintCardAction())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Stack...")
                .withShiftShortcut('P')
                .withDoMenuAction(new PrintStackAction())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Report...")
                .disabled()
                .build(this);

        if (!WyldCard.getInstance().getWindowManager().isMacOsTheme()) {

            this.addSeparator();

            MenuItemBuilder.ofDefaultType()
                    .named("Quit HyperCard")
                    .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().closeAllStacks(new ExecutionContext()))
                    .withShortcut('Q')
                    .build(this);
        }
    }

    public static void reset() {
        instance = new FileMenu();
    }
}
