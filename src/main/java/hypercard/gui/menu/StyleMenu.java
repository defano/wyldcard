package hypercard.gui.menu;

import hypercard.context.ToolsContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StyleMenu extends JMenu {

    public StyleMenu() {
        super("Style");

        MenuItemBuilder.ofDefaultType()
                .named("Plain")
                .withAction(e -> ToolsContext.getInstance().setFontStyle(Font.PLAIN))
                .fontStyle(Font.PLAIN)
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Bold")
                .withAction(e -> ToolsContext.getInstance().setFontStyle(Font.BOLD))
                .fontStyle(Font.BOLD)
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Italic")
                .withAction(e -> ToolsContext.getInstance().setFontStyle(Font.ITALIC))
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
                .withAction(e -> ToolsContext.getInstance().setFontSize(9))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("10")
                .withAction(e -> ToolsContext.getInstance().setFontSize(10))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("12")
                .withAction(e -> ToolsContext.getInstance().setFontSize(12))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("14")
                .withAction(e -> ToolsContext.getInstance().setFontSize(14))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("18")
                .withAction(e -> ToolsContext.getInstance().setFontSize(18))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("24")
                .withAction(e -> ToolsContext.getInstance().setFontSize(24))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Other...")
                .disabled()
                .build(this);
    }
}
