package hypercard.gui.menu;

import hypercard.context.ToolsContext;

import javax.swing.*;
import java.awt.*;

public class FontMenu extends JMenu {

    public FontMenu() {
        super("Font");

        for (String thisFamily : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            MenuItemBuilder.ofDefaultType()
                    .named(thisFamily)
                    .withAction(e -> ToolsContext.getInstance().setFontFamily(thisFamily))
                    .fontFamily(thisFamily)
                    .build(this);
        }
    }
}
