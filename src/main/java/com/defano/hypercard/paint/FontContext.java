package com.defano.hypercard.paint;

import com.defano.hypercard.fonts.FontFactory;
import com.defano.hypercard.fonts.TextStyleSpecifier;
import com.defano.hypertalk.ast.common.Value;
import com.defano.jmonet.model.Provider;

import java.awt.*;

/**
 * Manages the text style selection context. This is more complicated than one would initially assume:
 *
 * 1. Some APIs deal only with {@link Font} objects (that support only plain, bold and italic) styles; other APIs deal
 * in terms of {@link javax.swing.text.AttributeSet} that support a wider range of styles and formats.
 *
 * 2. The UI mixes the menu metaphor. The Font and Style menus are used to change the current font selection and also
 * display the font and style of whatever text was last focused.
 *
 * 3. This design is limited in that it doesn't handle mixed styles well (that is, a text selection containing multiple
 * fonts, sizes or styles). In this case, we calculate the middle of the selection and update the menus and whatnot to
 * reflect the styling of the middle char in the selection.
 *
 * 4. Choosing a size, font family or style from a menu should change *only* that property of the selected item. It
 * should not also apply any other selections. (i.e., making a selection bold should not change the font of that
 * selection to whatever font was last selected by the user.
 *
 * To solve these problems, this object maintains two states: The state of the hilited font style (representing what
 * should be checkmarked in the menus), and the state of the active user selection (representing a choice made from a
 * menu or font chooser dialog).
 */
public class FontContext {

    private final static FontContext instance = new FontContext();

    // Font, size and style of last-focused text element
    private final Provider<TextStyleSpecifier> hilitedTextStyleProvider = new Provider<>(TextStyleSpecifier.fromFont(new Font("Arial", Font.PLAIN, 12)));

    // Font, size and style of last font, size and style actively chosen by the user from the menus or chooser dialog
    private final Provider<Value> selectedFontFamilyProvider = new Provider<>(new Value("Arial"));
    private final Provider<Value> selectedFontSizeProvider = new Provider<>(new Value("12"));
    private final Provider<Value> selectedFontStyleProvider = new Provider<>(new Value("plain"));

    // For JMonet use only; components should listen for and react to font, style and size changes individually.
    private final Provider<Font> selectedFontProvider = new Provider<>(FontFactory.byNameStyleSize("Arial", Font.PLAIN, 12));

    public static FontContext getInstance() {
        return instance;
    }

    private FontContext() {
        selectedFontFamilyProvider.addObserver((o, arg) -> {
            TextStyleSpecifier tss = hilitedTextStyleProvider.get();
            tss.setFontFamily(((Value) arg).stringValue());
            hilitedTextStyleProvider.set(tss);

            tss = TextStyleSpecifier.fromFont(selectedFontProvider.get());
            tss.setFontFamily(((Value) arg).stringValue());
            selectedFontProvider.set(tss.toFont());
        });

        selectedFontSizeProvider.addObserver((o, arg) -> {
            TextStyleSpecifier tss = hilitedTextStyleProvider.get();
            tss.setFontSize(((Value) arg).integerValue());
            hilitedTextStyleProvider.set(tss);

            tss = TextStyleSpecifier.fromFont(selectedFontProvider.get());
            tss.setFontSize(((Value) arg).integerValue());
            selectedFontProvider.set(tss.toFont());
        });

        selectedFontStyleProvider.addObserver((o, arg) -> {
            TextStyleSpecifier tss = hilitedTextStyleProvider.get();
            tss.setFontStyle((Value) arg);
            hilitedTextStyleProvider.set(tss);

            tss = TextStyleSpecifier.fromFont(selectedFontProvider.get());
            tss.setFontStyle((Value) arg);
            selectedFontProvider.set(tss.toFont());
        });
    }

    public Provider<TextStyleSpecifier> getHilitedTextStyleProvider() {
        return hilitedTextStyleProvider;
    }

    public void setHilitedTextStyle(TextStyleSpecifier tss) {
        hilitedTextStyleProvider.set(tss);
    }

    public void setSelectedFont(Font font) {
        if (font != null) {
            TextStyleSpecifier tss = TextStyleSpecifier.fromFont(font);

            setSelectedFontFamily(tss.getFontFamily());
            setSelectedFontSize(tss.getFontSize());
            setSelectedFontStyle(tss.getHyperTalkStyle());
        }
    }

    public void setSelectedFontSize(int size) {
        selectedFontSizeProvider.set(new Value(size));
    }

    public void toggleSelectedFontStyle(Value style) {
        TextStyleSpecifier tss = TextStyleSpecifier.fromHyperTalkFontStyle(hilitedTextStyleProvider.get().getHyperTalkStyle());
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

    public Provider<Font> getSelectedFontProvider() {
        return selectedFontProvider;
    }

}
