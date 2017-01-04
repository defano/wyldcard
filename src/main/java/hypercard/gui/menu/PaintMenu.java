package hypercard.gui.menu;

import hypercard.context.ToolsContext;
import hypercard.paint.model.PaintToolType;
import hypercard.paint.tools.AbstractPaintTool;

import javax.swing.*;

public class PaintMenu extends JMenu {

    public PaintMenu() {
        super("Paint");

        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getPaintToolProvider().addObserver((oldValue, newValue) -> {
            AbstractPaintTool selectedTool = (AbstractPaintTool) newValue;
            PaintMenu.this.setVisible(selectedTool.getToolType() != PaintToolType.ARROW);
        });

        MenuItemBuilder.ofDefaultType()
                .named("Select")
                .withShortcut('S')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Select All")
                .withShortcut('A')
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Fill")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Invert")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pickup")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Darken")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Lighten")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Trace Edges")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Left")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Right")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Vertical")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Horizontal")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Opaque")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Transparent")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Keep")
                .withShortcut('K')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Revert")
                .disabled()
                .build(this);
    }
}