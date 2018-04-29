package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.window.WindowManager;
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

        JMenuItem grid = MenuItemBuilder.ofCheckType()
                .named("Grid")
                .withAction(a -> ToolsContext.getInstance().setGridSpacing(ToolsContext.getInstance().getGridSpacing() == 8 ? 1 : 8))
                .withCheckmarkProvider(ToolsContext.getInstance().getGridSpacingProvider().map(t -> t == 8))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Magnifier")
                .withAction(a -> ToolsContext.getInstance().toggleMagnifier())
                .withCheckmarkProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.MAGNIFIER || WyldCard.getInstance().getFocusedCard().getCanvas().getScale() != 1.0))
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

        MenuItemBuilder.ofDefaultType()
                .named("Spray Intensity...")
                .withAction(a -> WindowManager.getInstance().getIntensityPalette().setVisible(true))
                .withDisabledProvider(WindowManager.getInstance().getIntensityPalette().getWindowVisibleProvider())
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
