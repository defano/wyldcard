package hypercard.gui.menu;

import hypercard.context.ToolsContext;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.AbstractPaintTool;

import javax.swing.*;

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
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Power Keys")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Line Size...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Brush Shape...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Edit Pattern...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon Sides")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Draw Filled")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Draw Centered")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Draw Multiple")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Rotate")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Slant")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Distort")
                .withAction(e -> ToolsContext.getInstance().setSelectedToolType(PaintToolType.SLANT))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Perspective")
                .disabled()
                .build(this);
    }

}
