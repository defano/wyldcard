package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.WyldCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * The HyperCard Go menu.
 */
public class GoMenu extends WyldCardMenu {

    public static GoMenu instance = new GoMenu();

    private GoMenu() {
        super("Go");

        MenuItemBuilder.ofDefaultType()
                .named("Back")
                .withDoMenuAction(e -> WyldCard.getInstance().getNavigationManager().goBack(new ExecutionContext()))
                .withShortcut('\\')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Home")
                .withDoMenuAction(e -> WyldCard.getInstance().getNavigationManager().goStack(new ExecutionContext(), "Home", false, false))
                .withShortcut('H')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Help")
                .withDoMenuAction(e -> WyldCard.getInstance().getNavigationManager().goStack(new ExecutionContext(), "Help", false, false))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Recent")
                .withDoMenuAction(e -> WyldCard.getInstance().getWindowManager().showRecentCardsWindow())
                .withShortcut('R')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("First")
                .withDoMenuAction(e -> WyldCard.getInstance().getNavigationManager().goFirstCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('1')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Prev")
                .withDoMenuAction(e -> WyldCard.getInstance().getNavigationManager().goPrevCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('2')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next")
                .withDoMenuAction(e -> WyldCard.getInstance().getNavigationManager().goNextCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('3')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Last")
                .withDoMenuAction(e -> WyldCard.getInstance().getNavigationManager().goLastCard(new ExecutionContext(), WyldCard.getInstance().getStackManager().getFocusedStack()))
                .withShortcut('4')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Find...")
                .withDoMenuAction(e -> WyldCard.getInstance().getWindowManager().getMessageWindow().doFind())
                .withShortcut('F')
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Message")
                .withDoMenuAction(e -> WyldCard.getInstance().getWindowManager().getMessageWindow().toggleVisible())
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
                .withDoMenuAction(a -> WyldCard.getInstance().getWindowManager().nextWindow().getWindow().requestFocus())
                .withShortcut('L')
                .build(this);
    }

    public void reset() {
        instance = new GoMenu();
    }
}
