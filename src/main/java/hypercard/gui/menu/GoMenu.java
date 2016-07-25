package hypercard.gui.menu;

import hypercard.runtime.RuntimeEnv;

import javax.swing.*;

public class GoMenu extends JMenu {

    public GoMenu() {
        super("Go");

        MenuItemBuilder.ofDefaultType()
                .named("Back")
                .withAction(e -> RuntimeEnv.getRuntimeEnv().getStack().goBack())
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
                .withAction(e -> RuntimeEnv.getRuntimeEnv().getStack().goFirstCard())
                .withShortcut('1')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Prev")
                .withAction(e -> RuntimeEnv.getRuntimeEnv().getStack().goPrevCard())
                .withShortcut('2')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Next")
                .withAction(e -> RuntimeEnv.getRuntimeEnv().getStack().goNextCard())
                .withShortcut('3')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Last")
                .withAction(e -> RuntimeEnv.getRuntimeEnv().getStack().goLastCard())
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
                .withAction(e -> {
                    RuntimeEnv.getRuntimeEnv().setMessageBoxVisible(!RuntimeEnv.getRuntimeEnv().isMessageBoxVisible());
                    ((JCheckBoxMenuItem) e.getSource()).setState(RuntimeEnv.getRuntimeEnv().isMessageBoxVisible());
                })
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
