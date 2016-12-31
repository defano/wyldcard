package hypercard.gui.menu;

import hypercard.context.ToolsContext;
import hypercard.paint.tools.PaintToolType;
import hypercard.runtime.RuntimeEnv;

import javax.swing.*;
import java.awt.*;

public class ToolsMenu extends JMenu {

    public ToolsMenu() {
        super("Tools");

        MenuItemBuilder.ofDefaultType()
                .named("Tools Palette")
                .withAction(e -> RuntimeEnv.getRuntimeEnv().setPaintToolsPaletteVisible(!RuntimeEnv.getRuntimeEnv().isPaintToolsPaletteVisible()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Shapes Palette")
                .withAction(e -> RuntimeEnv.getRuntimeEnv().setShapesPaletteVisible(!RuntimeEnv.getRuntimeEnv().isShapesPaletteVisible()))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Finger")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.ARROW))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Button")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Field")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Selection")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.SELECTION))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pencil")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.PENCIL))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rectangle")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.RECTANGLE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Round Rectangle")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.ROUND_RECTANGLE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Oval")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.OVAL))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paintbrush")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.PAINTBRUSH))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Eraser")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.ERASER))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Line")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.LINE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.POLYGON))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Shape")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.SHAPE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Text")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.TEXT))
                .build(this);

    }
}
