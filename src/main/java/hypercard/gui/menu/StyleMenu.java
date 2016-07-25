package hypercard.gui.menu;

import javax.swing.*;
import java.awt.*;

public class StyleMenu extends JMenu {

    public StyleMenu() {
        super("Style");

        MenuItemBuilder.ofDefaultType()
                .named("Plain")
                .disabled()
                .fontStyle(Font.PLAIN)
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Bold")
                .disabled()
                .fontStyle(Font.BOLD)
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Italic")
                .disabled()
                .fontStyle(Font.ITALIC)
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Underline")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Outline")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Shadow")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Condense")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Extend")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Group")
                .disabled()
                .build(this);

        addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("9")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("10")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("12")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("14")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("18")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("24")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Other...")
                .disabled()
                .build(this);
    }
}
