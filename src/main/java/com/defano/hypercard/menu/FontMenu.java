package com.defano.hypercard.menu;

import com.defano.hypercard.paint.FontContext;
import com.defano.jmonet.model.ImmutableProvider;

import java.awt.*;

public class FontMenu extends HyperCardMenu {

    public static FontMenu instance = new FontMenu();

    private FontMenu() {
        super("Font");

        for (String thisFamily : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            MenuItemBuilder.ofCheckType()
                    .named(thisFamily)
                    .withAction(e -> FontContext.getInstance().setSelectedFontFamily(thisFamily))
                    .withCheckmarkProvider(ImmutableProvider.derivedFrom(FontContext.getInstance().getHilitedTextStyleProvider(), f -> thisFamily.equalsIgnoreCase(f.getFontFamily())))
                    .fontFamily(thisFamily)
                    .build(this);
        }
    }

    public void reset() {
        instance = new FontMenu();
    }
}
