package com.defano.hypercard.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.window.WindowManager;

public class GoMenu extends HyperCardMenu {

    public static GoMenu instance = new GoMenu();

    private GoMenu() {
        super("Go");

        MenuItemBuilder.ofDefaultType()
                .named("Back")
                .withAction(e -> HyperCard.getInstance().getStack().popCard(null))
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
                .withAction(e -> HyperCard.getInstance().getStack().goFirstCard(null))
                .withShortcut('1')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Prev")
                .withAction(e -> HyperCard.getInstance().getStack().goPrevCard(null))
                .withShortcut('2')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next")
                .withAction(e -> HyperCard.getInstance().getStack().goNextCard(null))
                .withShortcut('3')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Last")
                .withAction(e -> HyperCard.getInstance().getStack().goLastCard(null))
                .withShortcut('4')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Find...")
                .disabled()
                .withShortcut('F')
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Message")
                .withAction(e -> WindowManager.getMessageWindow().toggleVisible())
                .withCheckmarkProvider(WindowManager.getMessageWindow().getWindowVisibleProvider())
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
