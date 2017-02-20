/*
 * FileMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.Serializer;
import com.defano.hypercard.parts.model.StackModel;
import com.defano.hypercard.HyperCard;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class FileMenu extends HyperCardMenu {

    public final static FileMenu instance = new FileMenu();

    private FileMenu() {
        super("File");

        MenuItemBuilder.ofDefaultType()
                .named("New Stack...")
                .withAction(e -> HyperCard.getInstance().setStack(StackModel.newStack("Untitled")))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Open Stack...")
                .withAction(e -> {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        StackModel model = Serializer.deserialize(chooser.getSelectedFile(), StackModel.class);
                        HyperCard.getInstance().setStack(model);
                    }
                })
                .withShortcut('O')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Close Stack")
                .withShortcut('W')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Save Stack...")
                .withAction(e -> {
                    try {
                        JFileChooser chooser = new JFileChooser();
                        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            Serializer.serialize(chooser.getSelectedFile(), HyperCard.getInstance().getStack());
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                })
                .withShortcut('S')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Compact Stack")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Protect Stack...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Stack...")
                .disabled()
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Page Setup...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Field...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Card")
                .withShortcut('P')
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Stack...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Print Report...")
                .disabled()
                .build(this);
    }
}
