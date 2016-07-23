package hypercard.gui.menu;

import hypercard.Serializer;
import hypercard.gui.window.StackWindow;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.CardPart;
import hypercard.parts.model.CardModel;
import hypercard.runtime.RuntimeEnv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class HyperCardMenuBar extends JMenuBar {

    public HyperCardMenuBar() {
        add(buildFileMenu());
        add(buildEditMenu());
        add(buildGoMenu());
        add(buildToolsMenu());
        add(buildObjectsMenu());
        add(buildFontMenu());
        add(buildStyleMenu());
    }

    private JMenu buildStyleMenu () {
        JMenu style = new JMenu("Style");

        MenuItemBuilder.ofDefaultType()
                .named("Plain")
                .disabled()
                .fontStyle(Font.PLAIN)
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("Bold")
                .disabled()
                .fontStyle(Font.BOLD)
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("Italic")
                .disabled()
                .fontStyle(Font.ITALIC)
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("Underline")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("Outline")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("Shadow")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("Condense")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("Extend")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("Group")
                .disabled()
                .build(style);

        style.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("9")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("10")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("12")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("14")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("18")
                .disabled()
                .build(style);

        MenuItemBuilder.ofDefaultType()
                .named("24")
                .disabled()
                .build(style);

        style.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Other...")
                .disabled()
                .build(style);


        return style;
    }

    private JMenu buildFontMenu () {
        JMenu font = new JMenu("Font");

        for (String thisFamily : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            MenuItemBuilder.ofDefaultType()
                    .named(thisFamily)
                    .fontFamily(thisFamily)
                    .disabled()
                    .build(font);
        }

        return font;
    }

    private JMenu buildObjectsMenu () {
        JMenu objects = new JMenu("Objects");

        MenuItemBuilder.ofDefaultType()
                .named("Button Info...")
                .disabled()
                .build(objects);

        MenuItemBuilder.ofDefaultType()
                .named("Field Info...")
                .disabled()
                .build(objects);

        MenuItemBuilder.ofDefaultType()
                .named("Card Info...")
                .disabled()
                .build(objects);

        MenuItemBuilder.ofDefaultType()
                .named("Background Info...")
                .disabled()
                .build(objects);

        MenuItemBuilder.ofDefaultType()
                .named("Stack Info...")
                .disabled()
                .build(objects);

        objects.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Bring Closer")
                .disabled()
                .withShortcut('+')
                .build(objects);

        MenuItemBuilder.ofDefaultType()
                .named("Send Further")
                .disabled()
                .withShortcut('-')
                .build(objects);

        objects.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Button")
                .disabled()
                .build(objects);

        MenuItemBuilder.ofDefaultType()
                .named("New Field")
                .disabled()
                .build(objects);

        MenuItemBuilder.ofDefaultType()
                .named("New Background")
                .disabled()
                .build(objects);


        return objects;
    }

    private JMenu buildToolsMenu () {
        JMenu tools = new JMenu("Tools");

        MenuItemBuilder.ofDefaultType()
                .named("Finger")
                .disabled()
                .build(tools);

        MenuItemBuilder.ofDefaultType()
                .named("Button")
                .disabled()
                .build(tools);

        MenuItemBuilder.ofDefaultType()
                .named("Field")
                .disabled()
                .build(tools);

        return tools;
    }

    private JMenu buildGoMenu () {
        JMenu go = new JMenu("Go");

        MenuItemBuilder.ofDefaultType()
                .named("Back")
                .disabled()
                .withShortcut('~')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Home")
                .disabled()
                .withShortcut('H')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Help")
                .disabled()
                .withShortcut('?')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Recent")
                .disabled()
                .withShortcut('R')
                .build(go);

        go.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("First")
                .disabled()
                .withShortcut('1')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Prev")
                .disabled()
                .withShortcut('2')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Next")
                .disabled()
                .withShortcut('3')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Last")
                .disabled()
                .withShortcut('4')
                .build(go);

        go.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Find...")
                .disabled()
                .withShortcut('F')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Message")
                .disabled()
                .withShortcut('M')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Scroll")
                .disabled()
                .withShortcut('E')
                .build(go);

        MenuItemBuilder.ofDefaultType()
                .named("Next Window")
                .disabled()
                .withShortcut('L')
                .build(go);

        return go;
    }

    private JMenu buildEditMenu () {
        JMenu edit = new JMenu("Edit");

        MenuItemBuilder.ofDefaultType()
                .named("Undo")
                .disabled()
                .withShortcut('Z')
                .build(edit);

        edit.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Cut")
                .disabled()
                .withShortcut('X')
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Copy")
                .disabled()
                .withShortcut('C')
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Paste")
                .disabled()
                .withShortcut('V')
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Clear")
                .disabled()
                .build(edit);

        edit.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Card")
                .disabled()
                .withShortcut('N')
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Card")
                .disabled()
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Cut Card")
                .disabled()
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Copy Card")
                .disabled()
                .build(edit);

        edit.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Text Style...")
                .disabled()
                .withShortcut('T')
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Background")
                .disabled()
                .withShortcut('B')
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Icon")
                .disabled()
                .withShortcut('I')
                .build(edit);

        edit.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Audio...")
                .disabled()
                .build(edit);

        MenuItemBuilder.ofDefaultType()
                .named("Audio Help")
                .disabled()
                .build(edit);

        return edit;
    }

    private JMenu buildFileMenu () {
        JMenu file = new JMenu("File");

        MenuItemBuilder.ofDefaultType()
                .named("New Stack...")
                .disabled()
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Open Stack...")
                .withAction(e -> {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            CardModel model = Serializer.deserialize(chooser.getSelectedFile(), CardModel.class);
                            RuntimeEnv.getRuntimeEnv().getStackWindow().setCurrentCard(CardPart.fromModel(model));
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                })
                .withShortcut('O')
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Close Stack")
                .withShortcut('W')
                .disabled()
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Save Stack...")
                .withAction(e -> {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            Serializer.serialize(chooser.getSelectedFile(), RuntimeEnv.getRuntimeEnv().getCard().getCardModel());
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                })
                .withShortcut('S')
                .build(file);

        file.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Compact Stack")
                .disabled()
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Protect Stack...")
                .disabled()
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Stack...")
                .disabled()
                .build(file);

        file.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Page Setup...")
                .disabled()
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Print Field...")
                .disabled()
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Print Card")
                .withShortcut('P')
                .disabled()
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Print Stack...")
                .disabled()
                .build(file);

        MenuItemBuilder.ofDefaultType()
                .named("Print Report...")
                .disabled()
                .build(file);

        file.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Quit HyperCard")
                .withAction(e -> System.exit(0))
                .withShortcut('Q')
                .build(file);

        return file;
    }

}
