package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.model.PaintToolType;

import javax.swing.*;
import java.util.Optional;

public class OptionsMenu extends HyperCardMenu {

    public static OptionsMenu instance = new OptionsMenu();

    private OptionsMenu() {
        super("Options");

        // Show this menu only when a paint tool is active
        WyldCard.getInstance().getToolsManager().getToolModeProvider().subscribe(toolMode -> OptionsMenu.this.setVisible(ToolMode.PAINT == toolMode));

        JMenuItem grid = MenuItemBuilder.ofCheckType()
                .named("Grid")
                .withAction(a -> WyldCard.getInstance().getToolsManager().setGridSpacing(WyldCard.getInstance().getToolsManager().getGridSpacing() == 8 ? 1 : 8))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getGridSpacingProvider().map(t -> t == 8))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Magnifier")
                .withAction(a -> WyldCard.getInstance().getWindowManager().getMagnifierPalette().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getMagnifierPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Power Keys")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Line Size...")
                .withAction(a -> WyldCard.getInstance().getWindowManager().getLinesPalette().setVisible(true))
                .withDisabledProvider(WyldCard.getInstance().getWindowManager().getLinesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Brush Shape...")
                .withAction(a -> WyldCard.getInstance().getWindowManager().getBrushesPalette().setVisible(true))
                .withDisabledProvider(WyldCard.getInstance().getWindowManager().getBrushesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Edit Pattern...")
                .withAction(a -> WyldCard.getInstance().getWindowManager().showPatternEditor())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon Sides...")
                .withAction(a -> WyldCard.getInstance().getWindowManager().getShapesPalette().setVisible(true))
                .withDisabledProvider(WyldCard.getInstance().getWindowManager().getShapesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Spray Intensity...")
                .withAction(a -> WyldCard.getInstance().getWindowManager().getIntensityPalette().setVisible(true))
                .withDisabledProvider(WyldCard.getInstance().getWindowManager().getIntensityPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Draw Filled")
                .withAction(a -> WyldCard.getInstance().getToolsManager().toggleShapesFilled())
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getShapesFilledProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Draw Centered")
                .withAction(a -> WyldCard.getInstance().getToolsManager().toggleDrawCentered())
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDrawCenteredProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Draw Multiple")
                .withAction(a -> WyldCard.getInstance().getToolsManager().toggleDrawMultiple())
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDrawMultipleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Scale")
                .withAction(e -> WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.SCALE, true))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.SCALE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rotate")
                .withAction(e -> WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.ROTATE, true))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.ROTATE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Slant")
                .withAction(e -> WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.SLANT, true))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.SLANT))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Distort")
                .withAction(e -> WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.PROJECTION, true))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.PROJECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Perspective")
                .withAction(e -> WyldCard.getInstance().getToolsManager().forceToolSelection(ToolType.PERSPECTIVE, true))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getPaintToolProvider().map(t -> t.getToolType() == PaintToolType.PERSPECTIVE))
                .build(this);
    }

    public void reset() {
        instance = new OptionsMenu();
    }

}
