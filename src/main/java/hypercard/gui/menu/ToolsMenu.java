package hypercard.gui.menu;

import hypercard.gui.window.PaintToolsPalette;
import hypercard.gui.window.WindowBuilder;
import hypercard.paint.PaintToolsManager;
import hypercard.paint.tools.ToolType;
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
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.ARROW))
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
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.SELECTION))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pencil")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.PENCIL))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rectangle")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.RECTANGLE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Round Rectangle")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.ROUND_RECTANGLE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paintbrush")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.PAINTBRUSH))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Eraser")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.ERASER))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Line")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.LINE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon")
                .withAction(e -> PaintToolsManager.getInstance().setSelectedToolType(ToolType.POLYGON))
                .build(this);
    }
}
