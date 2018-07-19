package com.defano.wyldcard.menubar.main;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.runtime.context.FontContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.layouts.FontSizePicker;

import java.awt.*;

/**
 * The HyperCard Style menu.
 */
public class StyleMenu extends HyperCardMenu {

    public static StyleMenu instance = new StyleMenu();

    private StyleMenu() {
        super("Style");

        MenuItemBuilder.ofCheckType()
                .named("Align Left")
                .withAction(e -> FontContext.getInstance().setSelectedFontAlign(new Value("left")))
                .withDisabledProvider(ToolsContext.getInstance().getToolModeProvider().map(toolMode -> toolMode == ToolMode.PAINT))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedLeftAlignProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Align Center")
                .withAction(e -> FontContext.getInstance().setSelectedFontAlign(new Value("center")))
                .withDisabledProvider(ToolsContext.getInstance().getToolModeProvider().map(toolMode -> toolMode == ToolMode.PAINT))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedCenterAlignProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Align Right")
                .withAction(e -> FontContext.getInstance().setSelectedFontAlign(new Value("right")))
                .withDisabledProvider(ToolsContext.getInstance().getToolModeProvider().map(toolMode -> toolMode == ToolMode.PAINT))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedRightAlignProvider())
                .build(this);

        addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Plain")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("plain")))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedPlainProvider())
                .fontStyle(Font.PLAIN)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Bold")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("bold")))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedBoldProvider())
                .fontStyle(Font.BOLD)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Italic")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("italic")))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedItalicProvider())
                .fontStyle(Font.ITALIC)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Underline")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("underline")))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedUnderlineProvider())
                .withDisabledProvider(ToolsContext.getInstance().getToolModeProvider().map(value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Strikethrough")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("strikethrough")))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedStrikethroughProvider())
                .withDisabledProvider(ToolsContext.getInstance().getToolModeProvider().map(value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Superscript")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("superscript")))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedSuperscriptProvider())
                .withDisabledProvider(ToolsContext.getInstance().getToolModeProvider().map(value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Subscript")
                .withAction(e -> FontContext.getInstance().toggleSelectedFontStyle(new Value("subscript")))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedSubscriptProvider())
                .withDisabledProvider(ToolsContext.getInstance().getToolModeProvider().map(value -> value != ToolMode.BROWSE))
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
                .withCheckmarkProvider(FontContext.getInstance().getFocusedFontSizeProvider().map(e -> e.contains(new Value(9))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("10")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(10))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedFontSizeProvider().map(e -> e.contains(new Value(10))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("12")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(12))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedFontSizeProvider().map(e -> e.contains(new Value(12))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("14")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(14))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedFontSizeProvider().map(e -> e.contains(new Value(14))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("18")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(18))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedFontSizeProvider().map(e -> e.contains(new Value(18))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("24")
                .withAction(e -> FontContext.getInstance().setSelectedFontSize(24))
                .withCheckmarkProvider(FontContext.getInstance().getFocusedFontSizeProvider().map(e -> e.contains(new Value(24))))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Other...")
                .withCheckmarkProvider(FontContext.getInstance().getFocusedFontSizeProvider().map(e -> !e.contains(new Value(9)) && !e.contains(new Value(10)) && !e.contains(new Value(12)) && !e.contains(new Value(14)) && !e.contains(new Value(18)) && !e.contains(new Value(24))))
                .withAction(e ->
                        new WindowBuilder<>(new FontSizePicker())
                                .withModel(null)
                                .withTitle("Font Size")
                                .asModal()
                                .build())
                .build(this);
    }

    public void reset() {
        instance = new StyleMenu();
    }
}
