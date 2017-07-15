/*
 * ObjectsMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.gui.window.*;
import com.defano.hypercard.parts.ButtonPart;
import com.defano.hypercard.parts.FieldPart;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.hypercard.context.PartToolContext;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.HyperCard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class ObjectsMenu extends HyperCardMenu {

    public final static ObjectsMenu instance = new ObjectsMenu();

    private ObjectsMenu() {
        super("Objects");

        // Show this menu only when an object tool is active
        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate((o, arg) -> ObjectsMenu.this.setVisible(ToolMode.PAINT != arg));

        MenuItemBuilder.ofDefaultType()
                .named("Button Info...")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), value -> !(value instanceof ButtonPart)))
                .withAction(a -> PartToolContext.getInstance().getSelectedPartProvider().get().editProperties())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Field Info...")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), value -> !(value instanceof FieldPart)))
                .withAction(a -> PartToolContext.getInstance().getSelectedPartProvider().get().editProperties())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Card Info...")
                .withAction(e -> WindowBuilder.make(new CardPropertyEditor())
                        .withTitle("Card Properties")
                        .withModel(HyperCard.getInstance().getCard())
                        .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Background Info...")
                .withAction(e -> WindowBuilder.make(new BackgroundPropertyEditor())
                        .withTitle("Background Properties")
                        .withModel(HyperCard.getInstance().getCard())
                        .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Stack Info...")
                .withAction(e -> WindowBuilder.make(new StackPropertyEditor())
                        .withTitle("Stack Properties")
                        .withModel(HyperCard.getInstance().getStack().getStackModel())
                        .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                        .build())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Bring Closer")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), Objects::isNull))
                .withAction(a -> PartToolContext.getInstance().bringCloser())
                .withShortcut('+')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Send Further")
                .withDisabledProvider(ImmutableProvider.derivedFrom(PartToolContext.getInstance().getSelectedPartProvider(), Objects::isNull))
                .withAction(a -> PartToolContext.getInstance().sendFurther())
                .withShortcut('-')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Button")
                .withAction(e -> HyperCard.getInstance().getCard().newButton())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Field")
                .withAction(e -> HyperCard.getInstance().getCard().newField())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("New Background")
                .withAction(e -> HyperCard.getInstance().getStack().newBackground())
                .build(this);
    }
}
