/*
 * GoMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.WindowManager;

import javax.swing.*;

public class GoMenu extends JMenu {

    public GoMenu() {
        super("Go");

        MenuItemBuilder.ofDefaultType()
                .named("Back")
                .withAction(e -> HyperCard.getInstance().getStack().goBack())
                .withShortcut('~')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Home")
                .disabled()
                .withShortcut('H')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Help")
                .disabled()
                .withShortcut('?')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Recent")
                .disabled()
                .withShortcut('R')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("First")
                .withAction(e -> HyperCard.getInstance().getStack().goFirstCard())
                .withShortcut('1')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Prev")
                .withAction(e -> HyperCard.getInstance().getStack().goPrevCard())
                .withShortcut('2')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next")
                .withAction(e -> HyperCard.getInstance().getStack().goNextCard())
                .withShortcut('3')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Last")
                .withAction(e -> HyperCard.getInstance().getStack().goLastCard())
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
}
