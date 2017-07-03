/*
 * OptionsMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.base.AbstractSelectionTool;

import javax.swing.*;

public class OptionsMenu extends HyperCardMenu {

    public final static OptionsMenu instance = new OptionsMenu();

    private OptionsMenu() {
        super("Options");

        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate((o, arg) -> OptionsMenu.this.setVisible(ToolMode.PAINT == arg));

        JMenuItem grid = MenuItemBuilder.ofHeirarchicalType()
                .named("Grid")
                .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(1))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t != 1))
                .build(this);

                MenuItemBuilder.ofCheckType()
                        .named("2 px")
                        .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider().get() == 2 ? 1 : 2))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t == 2))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("5 px")
                        .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider().get() == 5 ? 1 : 5))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t == 5))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("10 px")
                        .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider().get() == 10 ? 1 : 10))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t == 10))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("20 px")
                        .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider().get() == 20 ? 1 : 20))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t == 20))
                        .build(grid);

        MenuItemBuilder.ofCheckType()
                .named("Magnifier")
                .withAction(a -> ToolsContext.getInstance().toggleMagnifier())
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.MAGNIFIER || HyperCard.getInstance().getCard().getCanvas().getScale() != 1.0))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Power Keys")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Line Size...")
                .withAction(a -> WindowManager.getLinesPalette().setShown(true))
                .withDisabledProvider(WindowManager.getLinesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Brush Shape...")
                .withAction(a -> WindowManager.getBrushesPalette().setShown(true))
                .withDisabledProvider(WindowManager.getBrushesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Edit Pattern...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon Sides...")
                .withAction(a -> WindowManager.getShapesPalette().setShown(true))
                .withDisabledProvider(WindowManager.getShapesPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Draw Filled")
                .withAction(a -> ToolsContext.getInstance().toggleShapesFilled())
                .withCheckmarkProvider(ToolsContext.getInstance().getShapesFilledProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Draw Centered")
                .withAction(a -> ToolsContext.getInstance().toggleDrawCentered())
                .withCheckmarkProvider(ToolsContext.getInstance().getDrawCenteredProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Draw Multiple")
                .withAction(a -> ToolsContext.getInstance().toggleDrawMultiple())
                .withCheckmarkProvider(ToolsContext.getInstance().getDrawMultipleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Scale")
                .withAction(e -> ToolsContext.getInstance().morphSelection(PaintToolType.SCALE))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), value -> !(value instanceof AbstractSelectionTool)))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.SCALE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rotate")
                .withAction(e -> ToolsContext.getInstance().morphSelection(PaintToolType.ROTATE))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), value -> !(value instanceof AbstractSelectionTool)))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.ROTATE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Slant")
                .withAction(e -> ToolsContext.getInstance().morphSelection(PaintToolType.SLANT))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), value -> !(value instanceof AbstractSelectionTool)))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.SLANT))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Distort")
                .withAction(e -> ToolsContext.getInstance().morphSelection(PaintToolType.PROJECTION))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), value -> !(value instanceof AbstractSelectionTool)))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.PROJECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Perspective")
                .withAction(e -> ToolsContext.getInstance().morphSelection(PaintToolType.PERSPECTIVE))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), value -> !(value instanceof AbstractSelectionTool)))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.PERSPECTIVE))
                .build(this);
    }

}
