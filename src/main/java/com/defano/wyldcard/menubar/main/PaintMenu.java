package com.defano.wyldcard.menubar.main;

import com.defano.jmonet.algo.dither.*;
import com.defano.jmonet.algo.fill.DefaultFillFunction;
import com.defano.jmonet.model.Interpolation;
import com.defano.jmonet.tools.selection.TransformableCanvasSelection;
import com.defano.jmonet.tools.selection.TransformableImageSelection;
import com.defano.jmonet.tools.selection.TransformableSelection;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.patterns.WyldCardPatternFactory;
import com.defano.wyldcard.runtime.context.DefaultToolsManager;

import javax.swing.*;

/**
 * The HyperCard Paint menu.
 */
public class PaintMenu extends HyperCardMenu {

    public static PaintMenu instance = new PaintMenu();

    private PaintMenu() {
        super("Paint");

        // Show this menu only when a paint tool is active
        WyldCard.getInstance().getToolsManager().getToolModeProvider().subscribe(toolMode -> PaintMenu.this.setVisible(ToolMode.PAINT == toolMode));

        MenuItemBuilder.ofDefaultType()
                .named("Select")
                .withShortcut('S')
                .withEnabledProvider(WyldCard.getInstance().getIsSelectableProvider())
                .withAction(a -> WyldCard.getInstance().getToolsManager().select())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Select All")
                .withShortcut('A')
                .withAction(a -> WyldCard.getInstance().getToolsManager().selectAll())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Fill")
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableImageSelectionProvider())
                .withAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).fill(WyldCardPatternFactory.getInstance().getPattern(WyldCard.getInstance().getToolsManager().getFillPattern()), new DefaultFillFunction()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Invert")
                .withAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).invert())
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableImageSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pickup")
                .withAction(e -> ((TransformableCanvasSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).pickupSelection())
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableCanvasSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Darken")
                .withAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).adjustBrightness(-20))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Lighten")
                .withAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).adjustBrightness(20))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Trace Edges")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Left")
                .withAction(e -> ((TransformableSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).rotateLeft())
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Right")
                .withAction(e -> ((TransformableSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).rotateRight())
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Vertical")
                .withAction(e -> ((TransformableSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).flipVertical())
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Horizontal")
                .withAction(e -> ((TransformableSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).flipHorizontal())
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("More Opaque")
                .withAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).adjustTransparency(20))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("More Transparent")
                .withAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).adjustTransparency(-20))
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
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

        this.addSeparator();

        JMenuItem antialiasingMenu = MenuItemBuilder.ofHierarchicalType()
                .named("Antialiasing")
                .build(this);

                MenuItemBuilder.ofCheckType()
                        .named("None")
                        .withAction(a -> WyldCard.getInstance().getToolsManager().setAntiAliasingMode(Interpolation.NONE))
                        .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getAntiAliasingProvider().map(m -> m == Interpolation.NONE))
                        .build(antialiasingMenu);

                antialiasingMenu.add(new JSeparator());

                MenuItemBuilder.ofCheckType()
                        .named("Default")
                        .withAction(a -> WyldCard.getInstance().getToolsManager().setAntiAliasingMode(Interpolation.DEFAULT))
                        .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getAntiAliasingProvider().map(m -> m == Interpolation.DEFAULT))
                        .build(antialiasingMenu);

                MenuItemBuilder.ofCheckType()
                        .named("Nearest Neighbor")
                        .withAction(a -> WyldCard.getInstance().getToolsManager().setAntiAliasingMode(Interpolation.NEAREST_NEIGHBOR))
                        .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getAntiAliasingProvider().map(m -> m == Interpolation.NEAREST_NEIGHBOR))
                        .build(antialiasingMenu);

                MenuItemBuilder.ofCheckType()
                        .named("Bilinear")
                        .withAction(a -> WyldCard.getInstance().getToolsManager().setAntiAliasingMode(Interpolation.BILINEAR))
                        .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getAntiAliasingProvider().map(m -> m == Interpolation.BILINEAR))
                        .build(antialiasingMenu);

                MenuItemBuilder.ofCheckType()
                        .named("Bicubic")
                        .withAction(a -> WyldCard.getInstance().getToolsManager().setAntiAliasingMode(Interpolation.BICUBIC))
                        .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getAntiAliasingProvider().map(m -> m == Interpolation.BICUBIC))
                        .build(antialiasingMenu);

        JMenuItem reduceColorMenu = MenuItemBuilder.ofHierarchicalType()
                .named("Reduce Color")
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().hasTransformableSelectionProvider())
                .build(this);

                MenuItemBuilder.ofDefaultType()
                        .named("Make Translucent Opaque")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).removeTranslucency(false))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("Make Translucent Transparent")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).removeTranslucency(true))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("Black & White")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceGreyscale(0, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("8 Grays")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceGreyscale(8, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("32 Grays")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceGreyscale(32, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("64 Grays")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceGreyscale(64, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("256 Grays")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceGreyscale(256, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("8 Colors")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceColor(8, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("32 Colors")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceColor(32, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("64 Colors")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceColor(64, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("256 Colors")
                        .withAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getToolsManager().getPaintTool()).reduceColor(256, WyldCard.getInstance().getToolsManager().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());


                JMenuItem ditherMenu = MenuItemBuilder.ofHierarchicalType()
                        .named("Dithering")
                        .build(reduceColorMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("None")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new NullDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof NullDitherer))
                                .build(ditherMenu);

                        ditherMenu.add(new JSeparator());

                        MenuItemBuilder.ofCheckType()
                                .named("Atkinson")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new AtkinsonDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof AtkinsonDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Burkes")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new BurkesDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof BurkesDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Floyd Steinberg")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new FloydSteinbergDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof FloydSteinbergDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Jarvis Judice Ninke")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new JarvisJudiceNinkeDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof JarvisJudiceNinkeDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new SierraDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof SierraDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra Two")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new SierraTwoDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof SierraTwoDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra Lite")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new SierraLiteDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof SierraLiteDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Stucki")
                                .withAction(a -> WyldCard.getInstance().getToolsManager().setDitherer(new StuckiDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().getDithererProvider().map(d -> d instanceof StuckiDitherer))
                                .build(ditherMenu);

    }

    public void reset() {
        instance = new PaintMenu();
    }
}