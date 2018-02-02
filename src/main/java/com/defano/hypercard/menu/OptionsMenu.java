package com.defano.hypercard.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.model.PaintToolType;

import javax.swing.*;
import java.util.Optional;

public class OptionsMenu extends HyperCardMenu {

    public static OptionsMenu instance = new OptionsMenu();

    private OptionsMenu() {
        super("Options");

        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getToolModeProvider().subscribe(toolMode -> OptionsMenu.this.setVisible(ToolMode.PAINT == toolMode));

        JMenuItem grid = MenuItemBuilder.ofHierarchicalType()
                .named("Grid")
                .withAction(a -> HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().setGridSpacing(1))
                .withCheckmarkProvider(HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().getGridSpacingObservable().map(t -> t != 1))
                .build(this);

                MenuItemBuilder.ofCheckType()
                        .named("2 px")
                        .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacing() == 2 ? 1 : 2))
                        .withCheckmarkProvider(ToolsContext.getInstance().getGridSpacingProvider().map(t -> t == 2))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("5 px")
                        .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacing() == 5 ? 1 : 5))
                        .withCheckmarkProvider(ToolsContext.getInstance().getGridSpacingProvider().map(t -> t == 5))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("8 px")
                        .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacing() == 8 ? 1 : 8))
                        .withCheckmarkProvider(ToolsContext.getInstance().getGridSpacingProvider().map(t -> t == 8))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("20 px")
                        .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacing() == 20 ? 1 : 20))
                        .withCheckmarkProvider(ToolsContext.getInstance().getGridSpacingProvider().map(t -> t == 20))
                        .build(grid);

        MenuItemBuilder.ofCheckType()
                .named("Magnifier")
                .withAction(a -> ToolsContext.getInstance().toggleMagnifier())
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.MAGNIFIER || HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().getScale() != 1.0))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Power Keys")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Line Size...")
                .withAction(a -> WindowManager.getInstance().getLinesPalette().setVisible(true))
                .withDisabledProvider(WindowManager.getInstance().getLinesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Brush Shape...")
                .withAction(a -> WindowManager.getInstance().getBrushesPalette().setVisible(true))
                .withDisabledProvider(WindowManager.getInstance().getBrushesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Edit Pattern...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon Sides...")
                .withAction(a -> WindowManager.getInstance().getShapesPalette().setVisible(true))
                .withDisabledProvider(WindowManager.getInstance().getShapesPalette().getWindowVisibleProvider())
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
                .named("Scale")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.SCALE, true))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.SCALE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rotate")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.ROTATE, true))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.ROTATE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Slant")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.SLANT, true))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.SLANT))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Distort")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.PROJECTION, true))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.PROJECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Perspective")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.PERSPECTIVE, true))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.PERSPECTIVE))
                .build(this);
    }

    public void reset() {
        instance = new OptionsMenu();
    }

}
