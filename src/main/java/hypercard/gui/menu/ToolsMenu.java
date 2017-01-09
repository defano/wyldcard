package hypercard.gui.menu;

import hypercard.context.ToolsContext;
import hypercard.paint.model.ImmutableProvider;
import hypercard.paint.tools.AbstractPaintTool;
import hypercard.paint.model.PaintToolType;
import hypercard.runtime.WindowManager;

import javax.swing.*;

public class ToolsMenu extends JMenu {

    public ToolsMenu() {
        super("Tools");

        MenuItemBuilder.ofCheckType()
                .named("Tools Palette")
                .withAction(e -> WindowManager.getPaintToolsPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getPaintToolsPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Patterns")
                .withAction(e -> WindowManager.getPatternsPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getPatternsPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Shapes")
                .withAction(e -> WindowManager.getShapesPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getShapesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Lines")
                .withAction(e -> WindowManager.getLinesPalette().toggleVisible())
                .withCheckmarkProvider(WindowManager.getLinesPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Finger")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.ARROW))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.ARROW))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Button")
                .disabled()
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Field")
                .disabled()
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Selection")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.SELECTION))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.SELECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Lasso")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.LASSO))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.LASSO))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Fill")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.FILL))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.FILL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Pencil")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.PENCIL))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.PENCIL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rectangle")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.RECTANGLE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.RECTANGLE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Round Rectangle")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.ROUND_RECTANGLE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.ROUND_RECTANGLE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Oval")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.OVAL))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.OVAL))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Paintbrush")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.PAINTBRUSH))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.PAINTBRUSH))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Spraypaint")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.AIRBRUSH))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.AIRBRUSH))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Eraser")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.ERASER))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.ERASER))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Line")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.LINE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.LINE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Curve")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.CURVE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.CURVE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Polygon")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.POLYGON))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.POLYGON))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Shape")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.SHAPE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.SHAPE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Text")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.TEXT))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> ((AbstractPaintTool) t).getToolType() == PaintToolType.TEXT))
                .build(this);

    }
}
