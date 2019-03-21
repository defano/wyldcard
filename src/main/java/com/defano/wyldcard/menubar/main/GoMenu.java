package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.runtime.context.ExecutionContext;

/**
 * The HyperCard Go menu.
 */
public class GoMenu extends HyperCardMenu {

    public static GoMenu instance = new GoMenu();

    private GoMenu() {
        super("Go");

        MenuItemBuilder.ofDefaultType()
                .named("Back")
                .withAction(e -> WyldCard.getInstance().getNavigationManager().goPopCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('\\')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Home")
                .withAction(e -> WyldCard.getInstance().getNavigationManager().goStack(new ExecutionContext(), "Home", false, false))
                .withShortcut('H')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Help")
                .withAction(e -> WyldCard.getInstance().getNavigationManager().goStack(new ExecutionContext(), "Help", false, false))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Recent")
                .withAction(e -> WyldCard.getInstance().getWindowManager().showRecentCardsWindow())
                .withShortcut('R')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("First")
                .withAction(e -> WyldCard.getInstance().getNavigationManager().goFirstCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('1')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Prev")
                .withAction(e -> WyldCard.getInstance().getNavigationManager().goPrevCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('2')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next")
                .withAction(e -> WyldCard.getInstance().getNavigationManager().goNextCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('3')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Last")
                .withAction(e -> WyldCard.getInstance().getNavigationManager().goLastCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('4')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Find...")
                .withAction(e -> WyldCard.getInstance().getWindowManager().getMessageWindow().doFind())
                .withShortcut('F')
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Message")
                .withAction(e -> WyldCard.getInstance().getWindowManager().getMessageWindow().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getMessageWindow().getWindowVisibleProvider())
                .withShortcut('M')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Scroll")
                .disabled()
                .withShortcut('E')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next Window")
                .withAction(a -> WyldCard.getInstance().getWindowManager().nextWindow().getWindow().requestFocus())
                .withShortcut('L')
                .build(this);
    }

    public void reset() {
        instance = new GoMenu();
    }
}
