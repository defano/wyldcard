package hypercard.gui.menu;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

public class MenuItemBuilder {

    private JMenuItem item;

    private MenuItemBuilder(JMenuItem item) {
        this.item = item;
    }

    public static MenuItemBuilder ofCheckType () {
        return new MenuItemBuilder(new JCheckBoxMenuItem());
    }

    public static MenuItemBuilder ofRadioType () {
        return new MenuItemBuilder(new JRadioButtonMenuItem());
    }

    public static MenuItemBuilder ofDefaultType () {
        return new MenuItemBuilder(new JMenuItem());
    }

    public MenuItemBuilder withAction (ActionListener action) {
        this.item.addActionListener(action);
        return this;
    }

    public MenuItemBuilder named (String name) {
        this.item.setName(name);
        this.item.setText(name);
        return this;
    }

    public MenuItemBuilder withShortcut (char shortcut) {
        this.item.setMnemonic(shortcut);
        this.item.setAccelerator(KeyStroke.getKeyStroke(shortcut, InputEvent.META_MASK));
        return this;
    }

    public void build (JMenu intoMenu) {
        intoMenu.add(item);
    }
}
