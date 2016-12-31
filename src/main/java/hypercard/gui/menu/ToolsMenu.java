package hypercard.gui.menu;

import hypercard.paint.PaintToolsManager;
import hypercard.paint.tools.PaintToolType;
import hypercard.runtime.RuntimeEnv;

import javax.swing.*;

public class ToolsMenu extends JMenu {

    public ToolsMenu() {
        super("Tools");

        MenuItemBuilder.ofDefaultType()
                .named("Show Tools Palette")
                .withAction(e -> RuntimeEnv.getRuntimeEnv().setPaintToolsPaletteVisible(!RuntimeEnv.getRuntimeEnv().isPaintToolsPaletteVisible()))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Finger")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.ARROW))
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
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.SELECTION))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pencil")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.PENCIL))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rectangle")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.RECTANGLE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Round Rectangle")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.ROUND_RECTANGLE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Oval")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.OVAL))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paintbrush")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.PAINTBRUSH))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Eraser")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.ERASER))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Line")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.LINE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.POLYGON))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Shape")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.SHAPE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Text")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(PaintToolType.TEXT))
                .build(this);

    }
}
