/*
 * PaintMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.context.ToolsContext;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.base.PaintTool;
import com.defano.jmonet.tools.base.AbstractSelectionTool;

import javax.swing.*;
import java.util.Objects;

public class PaintMenu extends JMenu {

    public PaintMenu() {
        super("Paint");

        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getPaintToolProvider().addObserver((oldValue, newValue) -> {
            PaintTool selectedTool = (PaintTool) newValue;
            PaintMenu.this.setVisible(selectedTool.getToolType() != PaintToolType.ARROW);
        });

        MenuItemBuilder.ofDefaultType()
                .named("Select")
                .withShortcut('S')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Select All")
                .withShortcut('A')
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Fill")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Invert")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pickup")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Darken")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Lighten")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Trace Edges")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Left")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).rotateLeft())
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Right")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).rotateRight())
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Vertical")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).flipVertical())
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Horizontal")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).flipHorizontal())
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Opaque")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Transparent")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Keep")
                .withShortcut('K')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Revert")
                .disabled()
                .build(this);
    }
}