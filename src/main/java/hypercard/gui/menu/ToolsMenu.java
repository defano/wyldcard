package hypercard.gui.menu;

import javax.swing.*;

public class ToolsMenu extends JMenu {

    public ToolsMenu() {
        super("Tools");

        MenuItemBuilder.ofDefaultType()
                .named("Finger")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Button")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Field")
                .disabled()
                .build(this);
    }
}
