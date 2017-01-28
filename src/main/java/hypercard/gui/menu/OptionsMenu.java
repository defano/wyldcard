package hypercard.gui.menu;

import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.PaintToolType;
import com.defano.jmonet.tools.base.PaintTool;
import hypercard.HyperCard;
import hypercard.context.ToolsContext;
import hypercard.runtime.WindowManager;

import javax.swing.*;

public class OptionsMenu extends JMenu {

    public OptionsMenu() {
        super("Options");

        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getPaintToolProvider().addObserver((oldValue, newValue) -> {
            PaintTool selectedTool = (PaintTool) newValue;
            OptionsMenu.this.setVisible(selectedTool.getToolType() != PaintToolType.ARROW);
        });

        JMenuItem grid = MenuItemBuilder.ofHeirarchicalType()
                .named("Grid")
                .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(1))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t != 1))
                .build(this);

                MenuItemBuilder.ofCheckType()
                        .named("2 px")
                        .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider().get() == 2 ? 1 : 2))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t == 2))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("5 px")
                        .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider().get() == 5 ? 1 : 5))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t == 5))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("10 px")
                        .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider().get() == 10 ? 1 : 10))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t == 10))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("20 px")
                        .withAction(a -> HyperCard.getInstance().getCard().getCanvas().setGridSpacing(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider().get() == 20 ? 1 : 20))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getCard().getCanvas().getGridSpacingProvider(), t -> t == 20))
                        .build(grid);

        MenuItemBuilder.ofCheckType()
                .named("Magnifier")
                .withAction(a -> ToolsContext.getInstance().selectPaintTool(PaintToolType.MAGNIFIER))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.MAGNIFIER))
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

        MenuItemBuilder.ofCheckType()
                .named("Draw Centered")
                .withAction(a -> ToolsContext.getInstance().toggleDrawCentered())
                .withCheckmarkProvider(ToolsContext.getInstance().getDrawCenteredProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Draw Multiple")
                .withAction(a -> ToolsContext.getInstance().toggleDrawMultiple())
                .withCheckmarkProvider(ToolsContext.getInstance().getDrawMultipleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Rotate")
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.ROTATE))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.ROTATE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Slant")
                .withAction(e -> ToolsContext.getInstance().selectPaintTool(PaintToolType.SLANT))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.SLANT))
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
