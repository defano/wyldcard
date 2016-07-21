package hypercard.gui.menu;

import hypercard.Serializer;
import hypercard.gui.window.StackWindow;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.CardPart;
import hypercard.parts.model.CardModel;
import hypercard.runtime.RuntimeEnv;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class HyperCardMenuBar extends JMenuBar {

    public HyperCardMenuBar() {
        JMenu edit = new JMenu("Edit");

        add(buildFileMenu());
        add(edit);
    }

    private JMenu buildFileMenu () {
        JMenu file = new JMenu("File");

        MenuItemBuilder.ofDefaultType()
                .named("Open Card...")
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
                .named("Save Card...")
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
                .named("Quit")
                .withAction(e -> System.exit(0))
                .withShortcut('Q')
                .build(file);

        return file;
    }

}
