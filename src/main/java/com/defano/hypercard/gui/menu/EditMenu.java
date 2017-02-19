/*
 * EditMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.context.GlobalContext;
import com.defano.hypercard.context.ToolsContext;

import javax.swing.*;

public class EditMenu extends JMenu {
    
    public EditMenu () {
        super("Edit");

        MenuItemBuilder.ofDefaultType()
                .named("Undo")
                .withShortcut('Z')
                .withAction(e -> GlobalContext.getContext().getCard().getCanvas().undo())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Redo")
                .withAction(e -> GlobalContext.getContext().getCard().getCanvas().redo())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Cut")
                .disabled()
                .withShortcut('X')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Copy")
                .disabled()
                .withShortcut('C')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paste")
                .disabled()
                .withShortcut('V')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Clear")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Card")
                .withAction(e -> HyperCard.getInstance().getStack().newCard())
                .withShortcut('N')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Card")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Cut Card")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Copy Card")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Text Style...")
                .disabled()
                .withShortcut('T')
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Background")
                .withCheckmarkProvider(ToolsContext.getInstance().isEditingBackgroundProvider())
                .withAction(e -> ToolsContext.getInstance().toggleIsEditingBackground())
                .withShortcut('B')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Icon")
                .disabled()
                .withShortcut('I')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Audio...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Audio Help")
                .disabled()
                .build(this);
    }
}
