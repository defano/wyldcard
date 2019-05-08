package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.hypertalk.ast.model.ToolType;
import com.defano.jmonet.model.PaintToolType;

import java.util.Optional;

public class OptionsMenu extends HyperCardMenu {

    public static OptionsMenu instance = new OptionsMenu();

    private OptionsMenu() {
        super("Options");

        // Show this menu only when a paint tool is active
        WyldCard.getInstance().getPaintManager().getToolModeProvider().subscribe(toolMode -> OptionsMenu.this.setVisible(ToolMode.PAINT == toolMode));

        MenuItemBuilder.ofCheckType()
                .named("Grid")
                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setGridSpacing(WyldCard.getInstance().getPaintManager().getGridSpacing() == 8 ? 1 : 8))
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getGridSpacingProvider().map(t -> t == 8))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Magnifier")
                .withDoMenuAction(a -> WyldCard.getInstance().getWindowManager().getMagnifierPalette().toggleVisible())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getMagnifierPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Power Keys")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Line Size...")
                .withDoMenuAction(a -> WyldCard.getInstance().getWindowManager().getLinesPalette().setVisible(true))
                .withDisabledProvider(WyldCard.getInstance().getWindowManager().getLinesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Brush Shape...")
                .withDoMenuAction(a -> WyldCard.getInstance().getWindowManager().getBrushesPalette().setVisible(true))
                .withDisabledProvider(WyldCard.getInstance().getWindowManager().getBrushesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Edit Pattern...")
                .withDoMenuAction(a -> WyldCard.getInstance().getWindowManager().showPatternEditor())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Polygon Sides...")
                .withDoMenuAction(a -> WyldCard.getInstance().getWindowManager().getShapesPalette().setVisible(true))
                .withDisabledProvider(WyldCard.getInstance().getWindowManager().getShapesPalette().getWindowVisibleProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Spray Intensity...")
                .withDoMenuAction(a -> WyldCard.getInstance().getWindowManager().getIntensityPalette().setVisible(true))
                .withDisabledProvider(WyldCard.getInstance().getWindowManager().getIntensityPalette().getWindowVisibleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Draw Filled")
                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().toggleShapesFilled())
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getShapesFilledProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Draw Centered")
                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().toggleDrawCentered())
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDrawCenteredProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Draw Multiple")
                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().toggleDrawMultiple())
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDrawMultipleProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Scale")
                .withDoMenuAction(e -> WyldCard.getInstance().getPaintManager().forceToolSelection(ToolType.SCALE, true))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getPaintToolProvider().map(t -> t.getPaintToolType() == PaintToolType.SCALE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Rotate")
                .withDoMenuAction(e -> WyldCard.getInstance().getPaintManager().forceToolSelection(ToolType.ROTATE, true))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getPaintToolProvider().map(t -> t.getPaintToolType() == PaintToolType.ROTATE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Slant")
                .withDoMenuAction(e -> WyldCard.getInstance().getPaintManager().forceToolSelection(ToolType.SLANT, true))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getPaintToolProvider().map(t -> t.getPaintToolType() == PaintToolType.SLANT))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Distort")
                .withDoMenuAction(e -> WyldCard.getInstance().getPaintManager().forceToolSelection(ToolType.PROJECTION, true))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getPaintToolProvider().map(t -> t.getPaintToolType() == PaintToolType.PROJECTION))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Perspective")
                .withDoMenuAction(e -> WyldCard.getInstance().getPaintManager().forceToolSelection(ToolType.PERSPECTIVE, true))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().getSelectedImageProvider().map(Optional::isPresent))
                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getPaintToolProvider().map(t -> t.getPaintToolType() == PaintToolType.PERSPECTIVE))
                .build(this);
    }

    public void reset() {
        instance = new OptionsMenu();
    }

}
