/*
 * StyleMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.context.ToolsContext;
import com.defano.jmonet.model.ImmutableProvider;

import javax.swing.*;
import java.awt.*;

public class StyleMenu extends HyperCardMenu {

    public final static StyleMenu instance = new StyleMenu();

    private StyleMenu() {
        super("Style");

        MenuItemBuilder.ofCheckType()
                .named("Plain")
                .withAction(e -> ToolsContext.getInstance().setFontStyle(Font.PLAIN))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getStyle() == Font.PLAIN))
                .fontStyle(Font.PLAIN)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Bold")
                .withAction(e -> ToolsContext.getInstance().setFontStyle(Font.BOLD))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getStyle() == Font.BOLD))
                .fontStyle(Font.BOLD)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Italic")
                .withAction(e -> ToolsContext.getInstance().setFontStyle(Font.ITALIC))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getStyle() == Font.ITALIC))
                .fontStyle(Font.ITALIC)
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Underline")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Outline")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Shadow")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Condense")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Extend")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Group")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("9")
                .withAction(e -> ToolsContext.getInstance().setFontSize(9))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getSize() == 9))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("10")
                .withAction(e -> ToolsContext.getInstance().setFontSize(10))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getSize() == 10))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("12")
                .withAction(e -> ToolsContext.getInstance().setFontSize(12))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getSize() == 12))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("14")
                .withAction(e -> ToolsContext.getInstance().setFontSize(14))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getSize() == 14))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("18")
                .withAction(e -> ToolsContext.getInstance().setFontSize(18))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getSize() == 18))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("24")
                .withAction(e -> ToolsContext.getInstance().setFontSize(24))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), e -> e.getSize() == 24))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Other...")
                .disabled()
                .build(this);
    }
}
