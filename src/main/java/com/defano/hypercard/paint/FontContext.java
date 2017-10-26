package com.defano.hypercard.paint;

import com.defano.hypercard.fonts.FontFactory;
import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.model.Provider;
import com.google.common.collect.Sets;

import java.awt.*;
import java.util.Set;


/**
 * Manages the text style selection context. This is more complicated than one would initially assume.
 *
 * Several HyperCard entities get text styling from this context: Buttons (when selected with the button tool), fields
 * (when selected by the field tool), text inside of a field (applying to the selected range of text), and the text
 * paint tool.
 *
 * Some APIs deal only with {@link Font} objects (which support only plain, bold and italic) styles; other APIs deal
 * in terms of {@link javax.swing.text.AttributeSet} that support a wider range of styles and formats.
 *
 * The Font and Style menus are used to change the current font selection, but also indicate the font and style of
 * whatever text entity was last focused. This produces a complicated observational pattern--text entities observe
 * selections from the menus, and the menus observe the style of the focused text.
 *
 * Choosing a size, font family or style from a menu should change only the property of the selected item. For example,
 * making a selection bold should not also change the font to whatever font is shown selected in the Font menu.
 *
 * To solve these problems, this object maintains two states: The state of the focused font style (representing what
 * should be check-marked in the menus), and the state of the last active user selection (representing a choice made
 * from a menu or font chooser dialog). Changes to the "selected" font styling update the "focused" style, but changes
 * to the "focused" style do not affect the "selected" style.
 */
public class FontContext {

    private final static FontContext instance = new FontContext();

    private final static String DEFAULT_FONT_FAMILY = "Arial";
    private final static String DEFAULT_FONT_STYLE = "plain";
    private final static int DEFAULT_FONT_STYLE_CONST = Font.PLAIN;
    private final static int DEFAULT_FONT_SIZE = 12;

    // Font, size and style of last-focused text element (focused element may contain a mix of sizes, fonts and styles)
    private final Provider<Set<Value>> focusedFontFamilyProvider = new Provider<>(Sets.newHashSet(new Value(DEFAULT_FONT_FAMILY)));
    private final Provider<Set<Value>> focusedFontSizeProvider = new Provider<>(Sets.newHashSet(new Value(DEFAULT_FONT_SIZE)));
    private final Provider<Value> focusedFontStyleProvider = new Provider<>(new Value(DEFAULT_FONT_STYLE));

    // Font, size and style of last font, size and style chosen by the user from the menus or chooser dialog
    private final Provider<Value> selectedFontFamilyProvider = new Provider<>(new Value(DEFAULT_FONT_FAMILY));
    private final Provider<Value> selectedFontSizeProvider = new Provider<>(new Value(DEFAULT_FONT_SIZE));
    private final Provider<Value> selectedFontStyleProvider = new Provider<>(new Value(DEFAULT_FONT_STYLE));

    // For JMonet use only; components should listen for and react to font, style and size changes individually.
    private final Provider<Font> paintFontProvider = new Provider<>(FontFactory.byNameStyleSize(DEFAULT_FONT_FAMILY, DEFAULT_FONT_STYLE_CONST, DEFAULT_FONT_SIZE));

    public static FontContext getInstance() {
        return instance;
    }

    private FontContext() {

        // Change in selected font should always change focused font
        selectedFontFamilyProvider.addObserver((o, arg) -> focusedFontFamilyProvider.set(Sets.newHashSet((Value) arg)));
        selectedFontSizeProvider.addObserver((o, arg) -> focusedFontSizeProvider.set(Sets.newHashSet((Value) arg)));
        selectedFontStyleProvider.addObserver((o, arg) -> focusedFontStyleProvider.set((Value) arg));

        selectedFontFamilyProvider.addObserver((o, arg) -> paintFontProvider.set(FontFactory.byNameStyleSize(String.valueOf(arg), getFocusedTextStyle().getAwtFontStyle(), getFocusedTextStyle().getFontSize())));
        selectedFontStyleProvider.addObserver((o, arg) -> paintFontProvider.set(FontFactory.byNameStyleSize(getFocusedTextStyle().getFontFamily(), TextStyleSpecifier.convertHyperTalkStyleToAwt((Value) arg), getFocusedTextStyle().getFontSize())));
        selectedFontSizeProvider.addObserver((o, arg) -> paintFontProvider.set(FontFactory.byNameStyleSize(getFocusedTextStyle().getFontFamily(), getFocusedTextStyle().getAwtFontStyle(), ((Value) arg).integerValue())));
    }

    /**
     * Convenience method to set the selected font context given an AWT font object.
     * @param font The selected font.
     */
    public void setSelectedFont(Font font) {
        if (font != null) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromFont(font);

            setSelectedFontFamily(tss.getFontFamily());
            setSelectedFontSize(tss.getFontSize());
            setSelectedFontStyle(tss.getHyperTalkStyle());
        }
    }

    public void setFocusedTextStyle(TextStyleSpecifier tss) {
        if (tss != null) {
            focusedFontFamilyProvider.set(Sets.newHashSet(new Value(tss.getFontFamily())));
            focusedFontSizeProvider.set(Sets.newHashSet(new Value(tss.getFontSize())));
            focusedFontStyleProvider.set(tss.getHyperTalkStyle());
        }
    }

    /**
     * Sets the selected font size.
     * @param size The font size.
     */
    public void setSelectedFontSize(int size) {
        selectedFontSizeProvider.set(new Value(size));
    }

    /**
     * Toggle the given style from the current style selection, that is, add the style if it's not already part of the
     * selection or remove it, if it is.
     * @param style The (single) style to toggle.
     */
    public void toggleSelectedFontStyle(Value style) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontStyle(focusedFontStyleProvider.get());
        tss.toggleFontStyle(style);
        selectedFontStyleProvider.set(tss.getHyperTalkStyle());
    }

    public void setSelectedFontStyle(Value style) {
        selectedFontStyleProvider.set(style);
    }

    public void setSelectedFontFamily(String fontName) {
        selectedFontFamilyProvider.set(new Value(fontName));
    }

    public Provider<Value> getSelectedFontFamilyProvider() {
        return selectedFontFamilyProvider;
    }

    public Provider<Value> getSelectedFontSizeProvider() {
        return selectedFontSizeProvider;
    }

    public Provider<Value> getSelectedFontStyleProvider() {
        return selectedFontStyleProvider;
    }

    public Provider<Font> getPaintFontProvider() {
        return paintFontProvider;
    }

    public Provider<Set<Value>> getFocusedFontFamilyProvider() {
        return focusedFontFamilyProvider;
    }

    public Provider<Set<Value>> getFocusedFontSizeProvider() {
        return focusedFontSizeProvider;
    }

    public Provider<Value> getFocusedFontStyleProvider() {
        return focusedFontStyleProvider;
    }

    /**
     * Gets a "single" text style representing the current focus. Careful: This method is inherently lossy. The current
     * focused text may contain a mixture of fonts, sizes, and styles. The method reduces that selection to a single
     * font family, size, and set of styles.
     *
     * Useful for methods that need to reduce a composite style selection to a single value (for example, to be
     * displayed as default selections in a font-picker).
     *
     * @return A lossy, single-style representation of the focused text style.
     */
    public TextStyleSpecifier getFocusedTextStyle() {
        return TextStyleSpecifier.fromNameStyleSize((Value) focusedFontFamilyProvider.get().toArray()[0], focusedFontStyleProvider.get(), (Value) focusedFontSizeProvider.get().toArray()[0]);
    }
}
