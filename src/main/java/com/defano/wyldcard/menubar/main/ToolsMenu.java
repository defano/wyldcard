package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
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
                .withAction(e -> WyldCard.getInstance().getWindowManager().getPaintToolsPalette().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getPaintToolsPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Patterns")
                .withAction(e -> WyldCard.getInstance().getWindowManager().getPatternsPalette().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getPatternsPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Colors")
                .withAction(e -> WyldCard.getInstance().getWindowManager().getColorPalette().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getColorPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Shapes")
                .withAction(e -> WyldCard.getInstance().getWindowManager().getShapesPalette().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getShapesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Brushes")
                .withAction(e -> WyldCard.getInstance().getWindowManager().getBrushesPalette().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getBrushesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Lines")
                .withAction(e -> WyldCard.getInstance().getWindowManager().getLinesPalette().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getLinesPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Browse")
                .withIcon(new ImageIcon(getClass().getResource("/icons/finger.png")))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getToolModeProvider().map(value -> value == ToolMode.BROWSE))
                .withAction(a -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Button")
                .withIcon(new ImageIcon(getClass().getResource("/icons/button.png")))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getToolModeProvider().map(value -> value == ToolMode.BUTTON))
                .withAction(a -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.BUTTON))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Field")
                .withIcon(new ImageIcon(getClass().getResource("/icons/field.png")))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getToolModeProvider().map(value -> value == ToolMode.FIELD))
                .withAction(a -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.FIELD))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Select")
                .withIcon(new ImageIcon(getClass().getResource("/icons/selection.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.SELECT))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.SELECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Lasso")
                .withIcon(new ImageIcon(getClass().getResource("/icons/lasso.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.LASSO))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.LASSO))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Bucket")
                .withIcon(new ImageIcon(getClass().getResource("/icons/fill.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.BUCKET))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.FILL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Pencil")
                .withIcon(new ImageIcon(getClass().getResource("/icons/pencil.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.PENCIL))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.PENCIL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rectangle")
                .withIcon(new ImageIcon(getClass().getResource("/icons/rectangle.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.RECTANGLE))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.RECTANGLE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Round Rectangle")
                .withIcon(new ImageIcon(getClass().getResource("/icons/roundrect.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.ROUNDRECT))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.ROUND_RECTANGLE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Oval")
                .withIcon(new ImageIcon(getClass().getResource("/icons/oval.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.OVAL))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.OVAL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Brush")
                .withIcon(new ImageIcon(getClass().getResource("/icons/paintbrush.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.BRUSH))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.PAINTBRUSH))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Spray Can")
                .withIcon(new ImageIcon(getClass().getResource("/icons/spraypaint.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.SPRAY))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.AIRBRUSH))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Eraser")
                .withIcon(new ImageIcon(getClass().getResource("/icons/eraser.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.ERASER))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.ERASER))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Line")
                .withIcon(new ImageIcon(getClass().getResource("/icons/line.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.LINE))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.LINE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Curve")
                .withIcon(new ImageIcon(getClass().getResource("/icons/curve.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.CURVE))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.CURVE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Polygon")
                .withIcon(new ImageIcon(getClass().getResource("/icons/polygon.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.POLYGON))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.POLYGON))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Shape")
                .withIcon(new ImageIcon(getClass().getResource("/icons/shape.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.SHAPE))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.SHAPE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Text")
                .withIcon(new ImageIcon(getClass().getResource("/icons/text.png")))
                .withAction(e -> WyldCard.getInstance().getToolsManager().chooseTool(ToolType.TEXT))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.TEXT))
                .build(this);
    }

    public void reset() {
        instance = new ToolsMenu();
    }
}
