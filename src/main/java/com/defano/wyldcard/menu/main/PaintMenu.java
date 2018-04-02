package com.defano.wyldcard.menu.main;

import com.defano.wyldcard.menu.HyperCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.patterns.HyperCardPatternFactory;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.jmonet.algo.dither.*;
import com.defano.jmonet.tools.selection.TransformableCanvasSelection;
import com.defano.jmonet.tools.selection.TransformableImageSelection;
import com.defano.jmonet.tools.selection.TransformableSelection;

import javax.swing.*;

/**
 * The HyperCard Paint menu.
 */
public class PaintMenu extends HyperCardMenu {

    public static PaintMenu instance = new PaintMenu();

    private PaintMenu() {
        super("Paint");

        // Show this menu only when a paint tool is active
        ToolsContext.getInstance().getToolModeProvider().subscribe(toolMode -> PaintMenu.this.setVisible(ToolMode.PAINT == toolMode));

        MenuItemBuilder.ofDefaultType()
                .named("Select")
                .withShortcut('S')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Select All")
                .withShortcut('A')
                .withAction(a -> ToolsContext.getInstance().selectAll())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Fill")
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableImageSelection))
                .withAction(e -> ((TransformableImageSelection) ToolsContext.getInstance().getPaintTool()).fill(HyperCardPatternFactory.getInstance().getPattern(ToolsContext.getInstance().getFillPattern())))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Invert")
                .withAction(e -> ((TransformableImageSelection) ToolsContext.getInstance().getPaintTool()).invert())
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pickup")
                .withAction(e -> ((TransformableCanvasSelection) ToolsContext.getInstance().getPaintTool()).pickupSelection())
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableCanvasSelection))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Darken")
                .withAction(e -> ((TransformableImageSelection) ToolsContext.getInstance().getPaintTool()).adjustBrightness(-20))
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Lighten")
                .withAction(e -> ((TransformableImageSelection) ToolsContext.getInstance().getPaintTool()).adjustBrightness(20))
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Trace Edges")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Left")
                .withAction(e -> ((TransformableSelection) ToolsContext.getInstance().getPaintTool()).rotateLeft())
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Right")
                .withAction(e -> ((TransformableSelection) ToolsContext.getInstance().getPaintTool()).rotateRight())
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Vertical")
                .withAction(e -> ((TransformableSelection) ToolsContext.getInstance().getPaintTool()).flipVertical())
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Horizontal")
                .withAction(e -> ((TransformableSelection) ToolsContext.getInstance().getPaintTool()).flipHorizontal())
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("More Opaque")
                .withAction(e -> ((TransformableImageSelection) ToolsContext.getInstance().getPaintTool()).adjustTransparency(20))
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("More Transparent")
                .withAction(e -> ((TransformableImageSelection) ToolsContext.getInstance().getPaintTool()).adjustTransparency(-20))
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
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

        JMenuItem reduceColorMenu = MenuItemBuilder.ofHierarchicalType()
                .named("Reduce Color")
                .withEnabledProvider(ToolsContext.getInstance().getPaintToolProvider().map(t -> t instanceof TransformableSelection))
                .build(this);

                MenuItemBuilder.ofDefaultType()
                        .named("Make Translucent Opaque")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).removeTranslucency(false))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("Make Translucent Transparent")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).removeTranslucency(true))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("Black & White")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(0, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("8 Grays")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(8, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("32 Grays")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(32, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("64 Grays")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(64, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("256 Grays")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(256, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("8 Colors")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceColor(8, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("32 Colors")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceColor(32, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("64 Colors")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceColor(64, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("256 Colors")
                        .withAction(p -> ((TransformableImageSelection)ToolsContext.getInstance().getPaintTool()).reduceColor(256, ToolsContext.getInstance().getDitherer()))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                JMenuItem ditherMenu = MenuItemBuilder.ofHierarchicalType()
                        .named("Dithering")
                        .build(reduceColorMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("None")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new NullDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof NullDitherer))
                                .build(ditherMenu);

                        ditherMenu.add(new JSeparator());

                        MenuItemBuilder.ofCheckType()
                                .named("Atkinson")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new AtkinsonDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof AtkinsonDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Burkes")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new BurkesDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof BurkesDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Floyd Steinberg")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new FloydSteinbergDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof FloydSteinbergDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Jarvis Judice Ninke")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new JarvisJudiceNinkeDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof JarvisJudiceNinkeDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new SierraDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof SierraDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra Two")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new SierraTwoDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof SierraTwoDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Sierra Lite")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new SierraLiteDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof SierraLiteDitherer))
                                .build(ditherMenu);

                        MenuItemBuilder.ofCheckType()
                                .named("Stucki")
                                .withAction(a -> ToolsContext.getInstance().setDitherer(new StuckiDitherer()))
                                .withCheckmarkProvider(ToolsContext.getInstance().getDithererProvider().map(d -> d instanceof StuckiDitherer))
                                .build(ditherMenu);

    }

    public void reset() {
        instance = new PaintMenu();
    }
}