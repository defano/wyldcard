package com.defano.hypercard.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.PaintToolType;

import javax.swing.*;
import java.util.Objects;

public class OptionsMenu extends HyperCardMenu {

    public static OptionsMenu instance = new OptionsMenu();

    private OptionsMenu() {
        super("Options");

        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getToolModeProvider().addObserverAndUpdate((o, arg) -> OptionsMenu.this.setVisible(ToolMode.PAINT == arg));

        JMenuItem grid = MenuItemBuilder.ofHeirarchicalType()
                .named("Grid")
                .withAction(a -> HyperCard.getInstance().getDisplayedCard().getCanvas().setGridSpacing(1))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getDisplayedCard().getCanvas().getGridSpacingProvider(), t -> t != 1))
                .build(this);

                MenuItemBuilder.ofCheckType()
                        .named("2 px")
                        .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacingProvider().get() == 2 ? 1 : 2))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getGridSpacingProvider(), t -> t == 2))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("5 px")
                        .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacingProvider().get() == 5 ? 1 : 5))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getGridSpacingProvider(), t -> t == 5))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("8 px")
                        .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacingProvider().get() == 8 ? 1 : 8))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getGridSpacingProvider(), t -> t == 8))
                        .build(grid);

                MenuItemBuilder.ofCheckType()
                        .named("20 px")
                        .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacingProvider().get() == 20 ? 1 : 20))
                        .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getGridSpacingProvider(), t -> t == 20))
                        .build(grid);

        MenuItemBuilder.ofCheckType()
                .named("Magnifier")
                .withAction(a -> ToolsContext.getInstance().toggleMagnifier())
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.MAGNIFIER || HyperCard.getInstance().getDisplayedCard().getCanvas().getScale() != 1.0))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Power Keys")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Line Size...")
                .withAction(a -> WindowManager.getLinesPalette().setVisible(true))
                .withDisabledProvider(WindowManager.getLinesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Brush Shape...")
                .withAction(a -> WindowManager.getBrushesPalette().setVisible(true))
                .withDisabledProvider(WindowManager.getBrushesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Edit Pattern...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon Sides...")
                .withAction(a -> WindowManager.getShapesPalette().setVisible(true))
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
                .named("Scale")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.SCALE, true))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.SCALE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rotate")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.ROTATE, true))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.ROTATE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Slant")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.SLANT, true))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.SLANT))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Distort")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.PROJECTION, true))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.PROJECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Perspective")
                .withAction(e -> ToolsContext.getInstance().forceToolSelection(ToolType.PERSPECTIVE, true))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getPaintToolProvider(), t -> t.getToolType() == PaintToolType.PERSPECTIVE))
                .build(this);
    }

    public void reset() {
        instance = new OptionsMenu();
    }

}
