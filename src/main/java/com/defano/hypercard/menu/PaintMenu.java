package com.defano.hypercard.menu;

import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.jmonet.tools.base.AbstractSelectionTool;

import javax.swing.*;
import java.util.Optional;

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
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Invert")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).invert())
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Pickup")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).pickupSelection())
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Darken")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).adjustBrightness(-20))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Lighten")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).adjustBrightness(20))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Trace Edges")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Left")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).rotateLeft())
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Rotate Right")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).rotateRight())
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Vertical")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).flipVertical())
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Flip Horizontal")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).flipHorizontal())
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("More Opaque")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).adjustTransparency(20))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("More Transparent")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).adjustTransparency(-20))
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
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
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .build(this);

                MenuItemBuilder.ofDefaultType()
                        .named("Black & White")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(0))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("8 Grays")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(8))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("32 Grays")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(32))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("64 Grays")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(64))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("256 Grays")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceGreyscale(256))
                        .build(reduceColorMenu);

                reduceColorMenu.add(new JSeparator());

                MenuItemBuilder.ofDefaultType()
                        .named("8 Colors")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceColor(8))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("32 Colors")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceColor(32))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("64 Colors")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceColor(64))
                        .build(reduceColorMenu);

                MenuItemBuilder.ofDefaultType()
                        .named("256 Colors")
                        .withAction(p -> ((AbstractSelectionTool)ToolsContext.getInstance().getPaintTool()).reduceColor(256))
                        .build(reduceColorMenu);
    }

    public void reset() {
        instance = new PaintMenu();
    }
}