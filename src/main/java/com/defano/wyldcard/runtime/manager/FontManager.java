package com.defano.wyldcard.runtime.manager;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.fonts.TextStyleSpecifier;
import io.reactivex.Observable;
import io.reactivex.subjects.Subject;

import java.awt.*;
import java.util.Collection;
import java.util.Set;

public interface FontManager {

    /**
     * Convenience method to set the selected font context given an AWT font object.
     *
     * @param font The selected font.
     */
    void setSelectedFont(Font font);

    /**
     * Toggle the given style from the current style selection, that is, add the style if it's not already part of the
     * selection or remove it, if it is.
     *
     * @param style The (single) style to toggle.
     */
    void toggleSelectedFontStyle(Value style);

    void setSelectedFontAlign(Value align);

    Observable<Value> getSelectedTextAlignProvider();

    Value getSelectedFontFamily();

    void setSelectedFontFamily(String fontName);

    Subject<Value> getSelectedFontFamilyProvider();

    Subject<Value> getSelectedFontSizeProvider();

    Value getSelectedFontSize();

    /**
     * Sets the selected font size.
     *
     * @param size The font size.
     */
    void setSelectedFontSize(int size);

    Subject<Value> getSelectedFontStyleProvider();

    Value getSelectedFontStyle();

    void setSelectedFontStyle(Value style);

    Subject<Font> getPaintFontProvider();

    Subject<Set<Value>> getFocusedFontFamilyProvider();

    Subject<Set<Value>> getFocusedFontSizeProvider();

    Subject<Boolean> getFocusedBoldProvider();

    Subject<Boolean> getFocusedItalicProvider();

    Subject<Boolean> getFocusedUnderlineProvider();

    Subject<Boolean> getFocusedStrikethroughProvider();

    Subject<Boolean> getFocusedSuperscriptProvider();

    Subject<Boolean> getFocusedSubscriptProvider();

    Subject<Boolean> getFocusedPlainProvider();

    Observable<Boolean> getFocusedLeftAlignProvider();

    Observable<Boolean> getFocusedRightAlignProvider();

    Observable<Boolean> getFocusedCenterAlignProvider();

    /**
     * Gets a "single" text style representing the current focus. Careful: This method is inherently lossy. The current
     * focused text may contain a mixture of fonts, sizes, and styles. The method reduces that selection to a single
     * font family, size, and set of styles.
     * <p>
     * Useful for methods that need to reduce a composite style selection to a single value (for example, to be
     * displayed as default selections in a font-picker).
     *
     * @return A lossy, single-style representation of the focused text style.
     */
    TextStyleSpecifier getFocusedTextStyle();

    /**
     * Set the focused font context given a {@link TextStyleSpecifier} object.
     *
     * @param tss The text style specifier.
     */
    void setFocusedTextStyle(TextStyleSpecifier tss);

    void setFocusedHyperTalkFontStyles(Collection<Value> values);

    void setFocusedTextAlign(Value align);
}
