package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;

/**
 * The HyperCard Go menu.
 */
public class GoMenu extends HyperCardMenu {

    public static GoMenu instance = new GoMenu();

    private GoMenu() {
        super("Go");

        MenuItemBuilder.ofDefaultType()
                .named("Back")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().popCard(new ExecutionContext(), null))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Home")
                .disabled()
                .withShortcut('H')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Help")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Recent")
                .disabled()
                .withShortcut('R')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("First")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().goFirstCard(new ExecutionContext(), null))
                .withShortcut('1')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Prev")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().goPrevCard(new ExecutionContext(), null))
                .withShortcut('2')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().goNextCard(new ExecutionContext(), null))
                .withShortcut('3')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Last")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().goLastCard(new ExecutionContext(), null))
                .withShortcut('4')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Find...")
                .withAction(e -> WindowManager.getInstance().getMessageWindow().doFind())
                .withShortcut('F')
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Message")
                .withAction(e -> WindowManager.getInstance().getMessageWindow().toggleVisible())
                .withCheckmarkProvider(WindowManager.getInstance().getMessageWindow().getWindowVisibleProvider())
                .withShortcut('M')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Scroll")
                .disabled()
                .withShortcut('E')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next Window")
                .disabled()
                .withShortcut('L')
                .build(this);
    }

    public void reset() {
        instance = new GoMenu();
    }
}
