package hypercard.gui.menu;

import hypercard.context.ToolsContext;
import hypercard.paint.observers.Provider;

import javax.swing.*;
import java.awt.*;

public class FontMenu extends JMenu {

    public FontMenu() {
        super("Font");

        for (String thisFamily : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            MenuItemBuilder.ofCheckType()
                    .named(thisFamily)
                    .withAction(e -> ToolsContext.getInstance().setFontFamily(thisFamily))
                    .withCheckmarkProvider(new Provider<>(ToolsContext.getInstance().getFontProvider(), f -> ((Font)f).getFamily().equalsIgnoreCase(thisFamily)))
                    .fontFamily(thisFamily)
                    .build(this);
        }
    }
}
