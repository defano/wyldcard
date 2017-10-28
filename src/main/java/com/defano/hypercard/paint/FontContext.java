package com.defano.hypercard.paint;

import com.defano.hypercard.fonts.FontFactory;
import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.model.Provider;
import com.google.common.collect.Sets;

import java.awt.*;
import java.util.Collection;
import java.util.Set;


/**
 * Manages the text style selection context. This is more complicated than one would initially assume.
 *
 * Several HyperCard entities get text styling from this context: Buttons (when selected with the button tool), fields
 * (when selected by the field tool), text inside of a field (applying to the selected range of text), and the text
 * paint tool.
 *
 * Some APIs deal only with {@link Font} objects that support only plain, bold and italic styles; other APIs deal
 * in terms of {@link javax.swing.text.AttributeSet} and support a wider range of styles and formats. Some APIs
 * improperly support certain styles (for example, {@link javax.swing.text.rtf.RTFEditorKit} does not correctly persist
 * sub/superscript styles).
 *
 * The Font and Style menus are used to change the current font selection, but they also indicate the font and style of
 * whatever text-containing entity was last focused. This produces a complicated observational pattern--text entities
 * observe selections from the menus, and the menus observe the style of focused text entities.
 *
 * Choosing a size, font family or style from a menu should change only the property selected. For example,
 * making a selection bold should not also change the font to whatever font is shown selected in the Font menu.
 *
 * Note that the "plain" style is an unusual case: Typically, it represents the absence of any other style. But, when
 * focusing text with multiple styles the "Plain" menu item can appear checked alongside other styles.
 *
 * To solve these problems, this object maintains two collections of state: The state of the focused font style
 * (representing what should be check-marked in the menus), and the state of the last active user selection
 * (representing a choice made from a menu or font chooser dialog). Changes to the "selected" font styling update the
 * "focused" style, but changes to the "focused" style do not affect the "selected" style.
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
    private final Provider<Boolean> focusedPlainProvider = new Provider<>(true);
    private final Provider<Boolean> focusedBoldProvider = new Provider<>(false);
    private final Provider<Boolean> focusedItalicProvider = new Provider<>(false);
    private final Provider<Boolean> focusedUnderlineProvider = new Provider<>(false);
    private final Provider<Boolean> focusedStrikethroughProvider = new Provider<>(false);
    private final Provider<Boolean> focusedSuperscriptProvider = new Provider<>(false);
    private final Provider<Boolean> focusedSubscriptProvider = new Provider<>(false);

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
        selectedFontStyleProvider.addObserver((o, arg) -> setFocusedFontStyle(TextStyleSpecifier.fromFontStyle((Value) arg), false));

        // Change in selected font should update paint tool
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

    /**
     * Set the focused font context given a {@link TextStyleSpecifier} object.
     * @param tss The text style specifier.
     */
    public void setFocusedTextStyle(TextStyleSpecifier tss) {
        if (tss != null) {
            focusedFontFamilyProvider.set(Sets.newHashSet(new Value(tss.getFontFamily())));
            focusedFontSizeProvider.set(Sets.newHashSet(new Value(tss.getFontSize())));

            focusedPlainProvider.set(tss.isPlain());
            focusedBoldProvider.set(tss.isBold());
            focusedItalicProvider.set(tss.isItalic());
            focusedUnderlineProvider.set(tss.isUnderline());
            focusedStrikethroughProvider.set(tss.isItalic());
            focusedSubscriptProvider.set(tss.isSubscript());
            focusedSuperscriptProvider.set(tss.isSubscript());
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
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontStyle(getFocusedHyperTalkFontStyle());
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

    public Provider<Boolean> getFocusedBoldProvider() {
        return focusedBoldProvider;
    }

    public Provider<Boolean> getFocusedItalicProvider() {
        return focusedItalicProvider;
    }

    public Provider<Boolean> getFocusedUnderlineProvider() {
        return focusedUnderlineProvider;
    }

    public Provider<Boolean> getFocusedStrikethroughProvider() {
        return focusedStrikethroughProvider;
    }

    public Provider<Boolean> getFocusedSuperscriptProvider() {
        return focusedSuperscriptProvider;
    }

    public Provider<Boolean> getFocusedSubscriptProvider() {
        return focusedSubscriptProvider;
    }

    public Provider<Boolean> getFocusedPlainProvider() {
        return focusedPlainProvider;
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
        return TextStyleSpecifier.fromNameStyleSize(
                (Value) focusedFontFamilyProvider.get().toArray()[0],
                getFocusedHyperTalkFontStyle(),
                (Value) focusedFontSizeProvider.get().toArray()[0]
        );
    }

    /**
     * Gets a HyperTalk representation of the currently focused font style selection; a comma-separated list of
     * styles, for example 'bold, italic' or 'plain'.
     *
     * @return
     */
    private Value getFocusedHyperTalkFontStyle() {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontStyle(new Value("plain"));
        tss.setBold(focusedBoldProvider.get());
        tss.setItalic(focusedItalicProvider.get());
        tss.setUnderline(focusedUnderlineProvider.get());
        tss.setStrikeThrough(focusedStrikethroughProvider.get());
        tss.setSubscript(focusedSubscriptProvider.get());
        tss.setSuperscript(focusedSuperscriptProvider.get());

        return tss.getHyperTalkStyle();
    }

    private void setFocusedFontStyle(TextStyleSpecifier tss, boolean includePlain) {
        focusedPlainProvider.set(tss.isPlain() || includePlain);
        focusedBoldProvider.set(tss.isBold());
        focusedItalicProvider.set(tss.isItalic());
        focusedUnderlineProvider.set(tss.isUnderline());
        focusedStrikethroughProvider.set(tss.isStrikeThrough());
        focusedSuperscriptProvider.set(tss.isSuperscript());
        focusedSubscriptProvider.set(tss.isSubscript());
    }

    public void setFocusedHyperTalkFontStyles(Collection<Value> values) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromFontStyle(new Value("plain"));
        boolean includesPlain = false;

        for (Value thisStyleSet : values) {
            tss.appendStyle(thisStyleSet);
            includesPlain |= tss.isPlain();
        }

        setFocusedFontStyle(tss, includesPlain);
    }
}
