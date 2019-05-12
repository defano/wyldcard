package com.defano.wyldcard.menu.main;

import com.defano.jmonet.tools.attributes.FillFunction;
import com.defano.jmonet.model.Interpolation;
import com.defano.jmonet.tools.selection.TransformableCanvasSelection;
import com.defano.jmonet.tools.selection.TransformableImageSelection;
import com.defano.jmonet.tools.selection.TransformableSelection;
import com.defano.jmonet.transform.dither.NullDitherer;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menu.WyldCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.pattern.WyldCardPatternFactory;

import javax.swing.*;

/**
 * The HyperCard Paint menu.
 */
public class PaintMenu extends WyldCardMenu {

    public static PaintMenu instance = new PaintMenu();

    private PaintMenu() {
        super("Paint");

        // Show this menu only when a paint tool is active
        WyldCard.getInstance().getPaintManager().getToolModeProvider().subscribe(toolMode -> PaintMenu.this.setVisible(ToolMode.PAINT == toolMode));

        MenuItemBuilder.ofDefaultType()
                .named("Select")
                .withShortcut('S')
                .withEnabledProvider(WyldCard.getInstance().getStackManager().getIsSelectableProvider())
                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().select())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Select All")
                .withShortcut('A')
                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().selectAll())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Fill")
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableImageSelectionProvider())
                .withDoMenuAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).fill(WyldCardPatternFactory.getInstance().getPattern(WyldCard.getInstance().getPaintManager().getFillPattern()), new FillFunction() {}))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Invert")
                .withDoMenuAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).invert())
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableImageSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pickup")
                .withDoMenuAction(e -> ((TransformableCanvasSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).pickupSelection())
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableCanvasSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Darken")
                .withDoMenuAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).adjustBrightness(-20))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Lighten")
                .withDoMenuAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).adjustBrightness(20))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Trace Edges")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Left")
                .withDoMenuAction(e -> ((TransformableSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).rotateLeft())
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Right")
                .withDoMenuAction(e -> ((TransformableSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).rotateRight())
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Vertical")
                .withDoMenuAction(e -> ((TransformableSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).flipVertical())
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Horizontal")
                .withDoMenuAction(e -> ((TransformableSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).flipHorizontal())
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("More Opaque")
                .withDoMenuAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).adjustTransparency(20))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("More Transparent")
                .withDoMenuAction(e -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).adjustTransparency(-20))
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
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
                        .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setAntiAliasingMode(Interpolation.NONE))
                        .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getAntiAliasingProvider().map(m -> m == Interpolation.NONE))
                        .build(antialiasingMenu);

                antialiasingMenu.add(new JSeparator());

                MenuItemBuilder.ofCheckType()
                        .named("Default")
                        .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setAntiAliasingMode(Interpolation.DEFAULT))
                        .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getAntiAliasingProvider().map(m -> m == Interpolation.DEFAULT))
                        .build(antialiasingMenu);

                MenuItemBuilder.ofCheckType()
                        .named("Nearest Neighbor")
                        .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setAntiAliasingMode(Interpolation.NEAREST_NEIGHBOR))
                        .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getAntiAliasingProvider().map(m -> m == Interpolation.NEAREST_NEIGHBOR))
                        .build(antialiasingMenu);

                MenuItemBuilder.ofCheckType()
                        .named("Bilinear")
                        .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setAntiAliasingMode(Interpolation.BILINEAR))
                        .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getAntiAliasingProvider().map(m -> m == Interpolation.BILINEAR))
                        .build(antialiasingMenu);

                MenuItemBuilder.ofCheckType()
                        .named("Bicubic")
                        .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setAntiAliasingMode(Interpolation.BICUBIC))
                        .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getAntiAliasingProvider().map(m -> m == Interpolation.BICUBIC))
                        .build(antialiasingMenu);

        JMenuItem reduceColorMenu = MenuItemBuilder.ofHierarchicalType()
                .named("Reduce Color")
                .withEnabledProvider(WyldCard.getInstance().getPaintManager().hasTransformableSelectionProvider())
                .build(this);

                MenuItemBuilder.ofDefaultType()
                        .named("Make Translucent Opaque")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).removeTranslucency(false))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("Make Translucent Transparent")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).removeTranslucency(true))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("Black & White")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceGreyscale(0, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("8 Grays")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceGreyscale(8, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("32 Grays")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceGreyscale(32, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("64 Grays")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceGreyscale(64, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("256 Grays")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceGreyscale(256, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("8 Colors")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceColor(8, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("32 Colors")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceColor(32, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("64 Colors")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceColor(64, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("256 Colors")
                        .withDoMenuAction(p -> ((TransformableImageSelection) WyldCard.getInstance().getPaintManager().getPaintTool()).reduceColor(256, WyldCard.getInstance().getPaintManager().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());


                JMenuItem ditherMenu = MenuItemBuilder.ofHierarchicalType()
                        .named("Dithering")
                        .build(reduceColorMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("None")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.NullDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof NullDitherer))
                                .build(ditherMenu);

                        ditherMenu.add(new JSeparator());

                        MenuItemBuilder.ofCheckType()
                                .named("Atkinson")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.AtkinsonDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof com.defano.jmonet.transform.dither.AtkinsonDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Burkes")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.BurkesDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof com.defano.jmonet.transform.dither.BurkesDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Floyd Steinberg")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.FloydSteinbergDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof com.defano.jmonet.transform.dither.FloydSteinbergDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Jarvis Judice Ninke")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.JarvisJudiceNinkeDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof com.defano.jmonet.transform.dither.JarvisJudiceNinkeDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.SierraDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof com.defano.jmonet.transform.dither.SierraDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra Two")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.SierraTwoDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof com.defano.jmonet.transform.dither.SierraTwoDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra Lite")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.SierraLiteDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof com.defano.jmonet.transform.dither.SierraLiteDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Stucki")
                                .withDoMenuAction(a -> WyldCard.getInstance().getPaintManager().setDitherer(new com.defano.jmonet.transform.dither.StuckiDitherer()))
                                .withCheckmarkProvider(WyldCard.getInstance().getPaintManager().getDithererProvider().map(d -> d instanceof com.defano.jmonet.transform.dither.StuckiDitherer))
                                .build(ditherMenu);

    }

    public void reset() {
        instance = new PaintMenu();
    }
}