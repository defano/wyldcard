package hypercard.gui.menu;

import hypercard.HyperCard;
import hypercard.context.ToolsContext;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.AbstractPaintTool;
import hypercard.runtime.WindowManager;

import javax.swing.*;
import java.awt.*;

public class OptionsMenu extends JMenu {

    public OptionsMenu() {
        super("Options");

        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getPaintToolProvider().addObserver((oldValue, newValue) -> {
            AbstractPaintTool selectedTool = (AbstractPaintTool) newValue;
            OptionsMenu.this.setVisible(selectedTool.getToolType() != PaintToolType.ARROW);
        });

        MenuItemBuilder.ofDefaultType()
                .named("Grid")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("FatBits")
                .withAction(a -> {
                    HyperCard.getRuntimeEnv().getCard().getCanvas().setScale(4.0);
                    HyperCard.getRuntimeEnv().getCard().getCanvas().setImageLocation(new Point(200,200));
                })
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

        MenuItemBuilder.ofDefaultType()
                .named("Draw Centered")
                .disabled()
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Draw Multiple")
                .withAction(a -> ToolsContext.getInstance().toggleDrawMultiple())
                .withCheckmarkProvider(ToolsContext.getInstance().getDrawMultipleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Rotate")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.ROTATE))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Slant")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.SLANT))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Distort")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Perspective")
                .disabled()
                .build(this);
    }

}
