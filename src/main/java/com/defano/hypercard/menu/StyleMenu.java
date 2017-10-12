package com.defano.hypercard.menu;

import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypercard.paint.FontContext;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.paint.ToolsContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.model.ImmutableProvider;
import com.l2fprod.common.swing.JFontChooser;

import java.awt.*;

public class StyleMenu extends HyperCardMenu {

    public static StyleMenu instance = new StyleMenu();

    private StyleMenu() {
        super("Style");

        MenuItemBuilder.ofCheckType()
                .named("Plain")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("plain")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isPlain))
                .fontStyle(Font.PLAIN)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Bold")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("bold")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isBold))
                .fontStyle(Font.BOLD)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Italic")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("italic")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isItalic))
                .fontStyle(Font.ITALIC)
                .build(this);
        
        MenuItemBuilder.ofCheckType()
                .named("Underline")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("underline")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isUnderline))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Strikethrough")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("strikethrough")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isStrikeThrough))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Superscript")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("superscript")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isSuperscript))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Subscript")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("subscript")))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), TextStyleSpecifier::isSubscript))
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
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(9))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 9))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("10")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(10))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 10))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("12")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(12))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 12))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("14")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(14))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 14))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("18")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(18))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 18))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("24")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(24))
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() == 24))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Other...")
                .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), e -> e.getFontSize() != 9 && e.getFontSize() != 10 && e.getFontSize() != 12 && e.getFontSize() != 14 && e.getFontSize() != 18 && e.getFontSize() != 24))
                .withAction(e -> FontContext.getInstance().setSelectedFont(JFontChooser.showDialog(WindowManager.getStackWindow(), "Choose Font", FontContext.getInstance().getHilitedTextStyleProvider().get().toFont())))
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getToolModeProvider(), value -> value != ToolMode.BROWSE))
                .build(this);
    }

    public void reset() {
        instance = new StyleMenu();
    }
}
