/*
 * ToolsMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.PaintToolType;
import com.defano.hypercard.context.ToolMode;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.runtime.WindowManager;

import javax.swing.*;

public class ToolsMenu extends HyperCardMenu {

    public final static ToolsMenu instance = new ToolsMenu();

    private ToolsMenu() {
        super("Tools");

        MenuItemBuilder.ofCheckType()
                .named("Tools")
                .withAction(e -> WindowManager.getPaintToolsPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getPaintToolsPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Patterns")
                .withAction(e -> WindowManager.getPatternsPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getPatternsPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Colors")
                .withAction(e -> WindowManager.getColorPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getColorPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Shapes")
                .withAction(e -> WindowManager.getShapesPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getShapesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Brushes")
                .withAction(e -> WindowManager.getBrushesPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getBrushesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Lines")
                .withAction(e -> WindowManager.getLinesPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getLinesPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Finger")
                .withIcon(new ImageIcon(getClass().getResource("/icons/finger.png")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value == ToolMode.BROWSE))
                .withAction(a -> ToolsContext.getInstance().setToolMode(ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Button")
                .withIcon(new ImageIcon(getClass().getResource("/icons/button.png")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value == ToolMode.BUTTON))
                .withAction(a -> ToolsContext.getInstance().setToolMode(ToolMode.BUTTON))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Field")
                .withIcon(new ImageIcon(getClass().getResource("/icons/field.png")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value == ToolMode.FIELD))
                .withAction(a -> ToolsContext.getInstance().setToolMode(ToolMode.FIELD))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Selection")
                .withIcon(new ImageIcon(getClass().getResource("/icons/selection.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.SELECTION))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.SELECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Lasso")
                .withIcon(new ImageIcon(getClass().getResource("/icons/lasso.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.LASSO))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.LASSO))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Fill")
                .withIcon(new ImageIcon(getClass().getResource("/icons/fill.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.FILL))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.FILL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Pencil")
                .withIcon(new ImageIcon(getClass().getResource("/icons/pencil.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.PENCIL))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.PENCIL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rectangle")
                .withIcon(new ImageIcon(getClass().getResource("/icons/rectangle.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.RECTANGLE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.RECTANGLE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Round Rectangle")
                .withIcon(new ImageIcon(getClass().getResource("/icons/roundrect.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.ROUND_RECTANGLE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.ROUND_RECTANGLE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Oval")
                .withIcon(new ImageIcon(getClass().getResource("/icons/oval.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.OVAL))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.OVAL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Paintbrush")
                .withIcon(new ImageIcon(getClass().getResource("/icons/paintbrush.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.PAINTBRUSH))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.PAINTBRUSH))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Air Brush")
                .withIcon(new ImageIcon(getClass().getResource("/icons/spraypaint.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.AIRBRUSH))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.AIRBRUSH))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Eraser")
                .withIcon(new ImageIcon(getClass().getResource("/icons/eraser.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.ERASER))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.ERASER))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Line")
                .withIcon(new ImageIcon(getClass().getResource("/icons/line.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.LINE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.LINE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Curve")
                .withIcon(new ImageIcon(getClass().getResource("/icons/curve.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.FREEFORM))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.CURVE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Polygon")
                .withIcon(new ImageIcon(getClass().getResource("/icons/polygon.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.POLYGON))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.POLYGON))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Shape")
                .withIcon(new ImageIcon(getClass().getResource("/icons/shape.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.SHAPE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.SHAPE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Text")
                .withIcon(new ImageIcon(getClass().getResource("/icons/text.png")))
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.TEXT))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.TEXT))
                .build(this);

    }
}
