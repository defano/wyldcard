package hypercard.gui.menu;

import javax.swing.*;
import java.awt.*;

public class FontMenu extends JMenu {

    public FontMenu() {
        super("Font");

        for (String thisFamily : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            MenuItemBuilder.ofDefaultType()
                    .named(thisFamily)
                    .fontFamily(thisFamily)
                    .disabled()
                    .build(this);
        }
    }
}
