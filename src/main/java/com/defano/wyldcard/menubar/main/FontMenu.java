package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
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
                    .withAction(e -> WyldCard.getInstance().getFontManager().setSelectedFontFamily(thisFamily))
                    .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontFamilyProvider().map(f -> f.contains(new Value(thisFamily))))
                    .fontFamily(thisFamily)
                    .build(this);
        }
    }

    public void reset() {
        instance = new FontMenu();
    }
}
