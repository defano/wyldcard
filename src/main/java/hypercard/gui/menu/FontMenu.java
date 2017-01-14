package hypercard.gui.menu;

import hypercard.context.ToolsContext;
import hypercard.paint.model.ImmutableProvider;

import javax.swing.*;
import java.awt.*;

public class FontMenu extends JMenu {

    public FontMenu() {
        super("Font");

        for (String thisFamily : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            MenuItemBuilder.ofCheckType()
                    .named(thisFamily)
                    .withAction(e -> ToolsContext.getInstance().setFontFamily(thisFamily))
                    .withCheckmarkProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getFontProvider(), f -> f.getFamily().equalsIgnoreCase(thisFamily)))
                    .fontFamily(thisFamily)
                    .build(this);
        }
    }
}
