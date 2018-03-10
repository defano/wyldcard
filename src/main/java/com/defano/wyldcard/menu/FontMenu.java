package com.defano.wyldcard.menu;

import com.defano.wyldcard.runtime.context.FontContext;
import com.defano.hypertalk.ast.model.Value;

import java.awt.*;

/**
 * The HyperCard Font menu.
 */
public class FontMenu extends HyperCardMenu {

    public static FontMenu instance = new FontMenu();

    private FontMenu() {
        super("Font");

        for (String thisFamily : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            MenuItemBuilder.ofCheckType()
                    .named(thisFamily)
                    .withAction(e -> FontContext.getInstance().setSelectedFontFamily(thisFamily))
                    .withCheckmarkProvider(FontContext.getInstance().getFocusedFontFamilyProvider().map(f -> f.contains(new Value(thisFamily))))
                    .fontFamily(thisFamily)
                    .build(this);
        }
    }

    public void reset() {
        instance = new FontMenu();
    }
}
