package com.defano.hypercard.menu;

import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.model.PaintToolType;

import javax.swing.*;

/**
 * The HyperCard Tools menu.
 */
public class ToolsMenu extends HyperCardMenu {

    public static ToolsMenu instance = new ToolsMenu();

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
                .named("Browse")
                .withIcon(new ImageIcon(getClass().getResource("/icons/finger.png")))
                .withCheckmarkProvider(ToolsContext.getInstance().getToolModeProvider().map(value -> value == ToolMode.BROWSE))
                .withAction(a -> ToolsContext.getInstance().chooseTool(ToolType.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Button")
                .withIcon(new ImageIcon(getClass().getResource("/icons/button.png")))
                .withCheckmarkProvider(ToolsContext.getInstance().getToolModeProvider().map(value -> value == ToolMode.BUTTON))
                .withAction(a -> ToolsContext.getInstance().chooseTool(ToolType.BUTTON))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Field")
                .withIcon(new ImageIcon(getClass().getResource("/icons/field.png")))
                .withCheckmarkProvider(ToolsContext.getInstance().getToolModeProvider().map(value -> value == ToolMode.FIELD))
                .withAction(a -> ToolsContext.getInstance().chooseTool(ToolType.FIELD))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Select")
                .withIcon(new ImageIcon(getClass().getResource("/icons/selection.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.SELECT))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.SELECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Lasso")
                .withIcon(new ImageIcon(getClass().getResource("/icons/lasso.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.LASSO))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.LASSO))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Bucket")
                .withIcon(new ImageIcon(getClass().getResource("/icons/fill.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.BUCKET))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.FILL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Pencil")
                .withIcon(new ImageIcon(getClass().getResource("/icons/pencil.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.PENCIL))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.PENCIL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rectangle")
                .withIcon(new ImageIcon(getClass().getResource("/icons/rectangle.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.RECTANGLE))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.RECTANGLE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Round Rectangle")
                .withIcon(new ImageIcon(getClass().getResource("/icons/roundrect.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.ROUNDRECT))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.ROUND_RECTANGLE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Oval")
                .withIcon(new ImageIcon(getClass().getResource("/icons/oval.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.OVAL))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.OVAL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Brush")
                .withIcon(new ImageIcon(getClass().getResource("/icons/paintbrush.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.BRUSH))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.PAINTBRUSH))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Spray Can")
                .withIcon(new ImageIcon(getClass().getResource("/icons/spraypaint.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.SPRAY))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.AIRBRUSH))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Eraser")
                .withIcon(new ImageIcon(getClass().getResource("/icons/eraser.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.ERASER))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.ERASER))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Line")
                .withIcon(new ImageIcon(getClass().getResource("/icons/line.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.LINE))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.LINE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Curve")
                .withIcon(new ImageIcon(getClass().getResource("/icons/curve.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.CURVE))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.CURVE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Polygon")
                .withIcon(new ImageIcon(getClass().getResource("/icons/polygon.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.POLYGON))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.POLYGON))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Shape")
                .withIcon(new ImageIcon(getClass().getResource("/icons/shape.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.SHAPE))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.SHAPE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Text")
                .withIcon(new ImageIcon(getClass().getResource("/icons/text.png")))
                .withAction(e -> ToolsContext.getInstance().chooseTool(ToolType.TEXT))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.TEXT))
                .build(this);
    }

    public void reset() {
        instance = new ToolsMenu();
    }
}
