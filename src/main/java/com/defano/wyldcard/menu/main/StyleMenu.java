package com.defano.wyldcard.menu.main;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menu.WyldCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.layout.FontSizePicker;

import java.awt.*;

/**
 * The HyperCard Style menu.
 */
public class StyleMenu extends WyldCardMenu {

    public static StyleMenu instance = new StyleMenu();

    private StyleMenu() {
        super("Style");

        MenuItemBuilder.ofCheckType()
                .named("Align Left")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontAlign(new Value("left")))
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(toolMode -> toolMode == ToolMode.PAINT))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedLeftAlignProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Align Center")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontAlign(new Value("center")))
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(toolMode -> toolMode == ToolMode.PAINT))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedCenterAlignProvider())
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Align Right")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontAlign(new Value("right")))
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(toolMode -> toolMode == ToolMode.PAINT))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedRightAlignProvider())
                .build(this);

        addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Plain")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().toggleSelectedFontStyle(new Value("plain")))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedPlainProvider())
                .fontStyle(Font.PLAIN)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Bold")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().toggleSelectedFontStyle(new Value("bold")))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedBoldProvider())
                .fontStyle(Font.BOLD)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Italic")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().toggleSelectedFontStyle(new Value("italic")))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedItalicProvider())
                .fontStyle(Font.ITALIC)
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Underline")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().toggleSelectedFontStyle(new Value("underline")))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedUnderlineProvider())
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Strikethrough")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().toggleSelectedFontStyle(new Value("strikethrough")))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedStrikethroughProvider())
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Superscript")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().toggleSelectedFontStyle(new Value("superscript")))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedSuperscriptProvider())
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(value -> value != ToolMode.BROWSE))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Subscript")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().toggleSelectedFontStyle(new Value("subscript")))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedSubscriptProvider())
                .withDisabledProvider(WyldCard.getInstance().getPaintManager().getToolModeProvider().map(value -> value != ToolMode.BROWSE))
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
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontSize(9))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().map(e -> e.contains(new Value(9))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("10")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontSize(10))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().map(e -> e.contains(new Value(10))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("12")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontSize(12))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().map(e -> e.contains(new Value(12))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("14")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontSize(14))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().map(e -> e.contains(new Value(14))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("18")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontSize(18))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().map(e -> e.contains(new Value(18))))
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("24")
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontSize(24))
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().map(e -> e.contains(new Value(24))))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofCheckType()
                .named("Other...")
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().map(e -> !e.contains(new Value(9)) && !e.contains(new Value(10)) && !e.contains(new Value(12)) && !e.contains(new Value(14)) && !e.contains(new Value(18)) && !e.contains(new Value(24))))
                .withDoMenuAction(e ->
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
