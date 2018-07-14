package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.layouts.RecentsWindow;

import javax.swing.*;

/**
 * The HyperCard Go menu.
 */
public class GoMenu extends HyperCardMenu {

    public static GoMenu instance = new GoMenu();

    private GoMenu() {
        super("Go");

        MenuItemBuilder.ofDefaultType()
                .named("Back")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().gotoPopCard(new ExecutionContext(), null))
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
                .withAction(e -> WindowBuilder.make(new RecentsWindow())
                        .withTitle("Recent Cards")
                        .asModal()
                        .withModel(WyldCard.getInstance().getFocusedStack())
                        .resizeable(true)
                        .setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE)
                        .build())
                .withShortcut('R')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("First")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().gotoFirstCard(new ExecutionContext(), null))
                .withShortcut('1')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Prev")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().gotoPrevCard(new ExecutionContext(), null))
                .withShortcut('2')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().gotoNextCard(new ExecutionContext(), null))
                .withShortcut('3')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Last")
                .withAction(e -> WyldCard.getInstance().getFocusedStack().gotoLastCard(new ExecutionContext(), null))
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
                .withAction(a -> WindowManager.getInstance().nextWindow().getWindow().requestFocus())
                .withShortcut('L')
                .build(this);
    }

    public void reset() {
        instance = new GoMenu();
    }
}
