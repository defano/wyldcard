/*
 * EditMenu
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.context.GlobalContext;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.parts.clipboard.CardActionListener;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.jmonet.clipboard.CanvasClipboardActionListener;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.tools.base.AbstractSelectionTool;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.util.Objects;
import com.defano.jmonet.model.ImmutableProvider;
import com.defano.jmonet.model.ProviderTransform;

public class EditMenu extends HyperCardMenu {

    public final static EditMenu instance = new EditMenu();

    private EditMenu () {
        super("Edit");

        // Routes cut/copy/paste actions to the correct canvas
        CanvasClipboardActionListener canvasActionListener = new CanvasClipboardActionListener(() -> HyperCard.getInstance().getCard().getCanvas());
        CardActionListener cardActionListener = new CardActionListener();

        MenuItemBuilder.ofDefaultType()
                .named("Undo")
                .withShortcut('Z')
                .withAction(e -> GlobalContext.getContext().getCard().getCanvas().undo())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Redo")
                .withAction(e -> GlobalContext.getContext().getCard().getCanvas().redo())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofAction(new DefaultEditorKit.CutAction())
                .named("Cut")
                .withActionListener(canvasActionListener)
                .withActionListener(cardActionListener)
                .withActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME))
                .withShortcut('X')
                .build(this);

        MenuItemBuilder.ofAction(new DefaultEditorKit.CopyAction())
                .named("Copy")
                .withActionListener(canvasActionListener)
                .withActionListener(cardActionListener)
                .withActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME))
                .withShortcut('C')
                .build(this);

        MenuItemBuilder.ofAction(new DefaultEditorKit.PasteAction())
                .named("Paste")
                .withActionListener(canvasActionListener)
                .withActionListener(cardActionListener)
                .withActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME))
                .withShortcut('V')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Clear")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).deleteSelection())
                .withDisabledProvider(ImmutableProvider.derivedFrom(ToolsContext.getInstance().getSelectedImageProvider(), Objects::isNull))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Card")
                .withAction(e -> HyperCard.getInstance().getStack().newCard())
                .withShortcut('N')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Card")
                .withDisabledProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getStack().getCardCountProvider(), c -> c < 2))
                .withAction(e -> HyperCard.getInstance().getStack().deleteCard())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Cut Card")
                .withDisabledProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getStack().getCardCountProvider(), c -> c < 2))
                .withAction(e -> HyperCard.getInstance().getStack().cutCard())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Copy Card")
                .withAction(e -> HyperCard.getInstance().getStack().copyCard())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paste Card")
                .withDisabledProvider(ImmutableProvider.derivedFrom(HyperCard.getInstance().getStack().getCardClipboardProvider(), Objects::isNull))
                .withAction(e -> HyperCard.getInstance().getStack().pasteCard())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Text Style...")
                .disabled()
                .withShortcut('T')
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Background")
                .withCheckmarkProvider(ToolsContext.getInstance().isEditingBackgroundProvider())
                .withAction(e -> ToolsContext.getInstance().toggleIsEditingBackground())
                .withShortcut('B')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Icon")
                .disabled()
                .withShortcut('I')
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Audio...")
                .disabled()
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Audio Help")
                .disabled()
                .build(this);

        this.addSeparator();

        JMenuItem laf = MenuItemBuilder.ofHeirarchicalType()
                .named("Look & Feel")
                .build(this);

        for (UIManager.LookAndFeelInfo thisLaf : UIManager.getInstalledLookAndFeels()) {

            MenuItemBuilder.ofCheckType()
                    .named(thisLaf.getName())
                    .withAction(a -> WindowManager.setLookAndFeel(thisLaf.getClassName()))
                    .withCheckmarkProvider(ImmutableProvider.derivedFrom(WindowManager.getLookAndFeelClassProvider(), value -> thisLaf.getClassName().equals(value)))
                    .build(laf);
        }

    }
}
