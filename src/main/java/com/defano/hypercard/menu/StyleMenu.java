package com.defano.hypercard.menu;

import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.ProviderTransform;
import com.l2fprod.common.swing.JFontChooser;

import java.awt.*;

public class StyleMenu extends HyperCardMenu {

    public static StyleMenu instance = new StyleMenu();

    private StyleMenu() {
        super("Style");

        MenuItemBuilder.ofCheckType()
                .named("Plain")
                .withAction(e -> ToolsContext.getInstance().toggleFontStyle(new Value("plain")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isPlain))
                .fontStyle(Font.PLAIN)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Bold")
                .withAction(e -> ToolsContext.getInstance().toggleFontStyle(new Value("bold")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isBold))
                .fontStyle(Font.BOLD)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Italic")
                .withAction(e -> ToolsContext.getInstance().toggleFontStyle(new Value("italic")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isItalic))
                .fontStyle(Font.ITALIC)
                .build(this);
        
        MenuItemBuilder.ofCheckType()
                .named("Underline")
                .withAction(e -> ToolsContext.getInstance().toggleFontStyle(new Value("underline")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isUnderline))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Strikethrough")
                .withAction(e -> ToolsContext.getInstance().toggleFontStyle(new Value("strikethrough")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isStrikeThrough))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Superscript")
                .withAction(e -> ToolsContext.getInstance().toggleFontStyle(new Value("superscript")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isSuperscript))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Subscript")
                .withAction(e -> ToolsContext.getInstance().toggleFontStyle(new Value("subscript")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isSubscript))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value != ToolMode.BROWSE))
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
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 9))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("10")
                .withAction(e -> ToolsContext.getInstance().setFontSize(10))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 10))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("12")
                .withAction(e -> ToolsContext.getInstance().setFontSize(12))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 12))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("14")
                .withAction(e -> ToolsContext.getInstance().setFontSize(14))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 14))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("18")
                .withAction(e -> ToolsContext.getInstance().setFontSize(18))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 18))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("24")
                .withAction(e -> ToolsContext.getInstance().setFontSize(24))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 24))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Other...")
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() != 9 && e.getFontSize() != 10 && e.getFontSize() != 12 && e.getFontSize() != 14 && e.getFontSize() != 18 && e.getFontSize() != 24))
                .withAction(e -> ToolsContext.getInstance().setFont(JFontChooser.showDialog(WindowManager.getStackWindow(), "Choose Font", ToolsContext.getInstance().getHilitedTextStyleProvider().get().toFont())))
                .build(this);
    }

    public void reset() {
        instance = new StyleMenu();
    }
}
