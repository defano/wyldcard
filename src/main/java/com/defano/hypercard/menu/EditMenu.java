package com.defano.hypercard.menu;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.clipboard.CardActionListener;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.window.WindowBuilder;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypercard.window.forms.IconCreator;
import com.defano.jmonet.clipboard.CanvasClipboardActionListener;
import com.defano.jmonet.tools.base.AbstractSelectionTool;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.util.Objects;
import java.util.Optional;

/**
 * The HyperCard Edit menu.
 */
public class EditMenu extends HyperCardMenu {

    public static EditMenu instance = new EditMenu();

    private EditMenu () {
        super("Edit");

        // Routes cut/copy/paste actions to the correct canvas
        CanvasClipboardActionListener canvasActionListener = new CanvasClipboardActionListener(() -> HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas());
        CardActionListener cardActionListener = new CardActionListener();

        MenuItemBuilder.ofDefaultType()
                .named("Undo")
                .withShortcut('Z')
                .withAction(e -> HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().undo())
                .withEnabledProvider(HyperCard.getInstance().getIsUndoableProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Redo")
                .withShiftShortcut('Z')
                .withAction(e -> HyperCard.getInstance().getActiveStackDisplayedCard().getCanvas().redo())
                .withEnabledProvider(HyperCard.getInstance().getIsRedoableProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofAction(new DefaultEditorKit.CutAction())
                .named("Cut")
                .withAction(canvasActionListener)
                .withAction(cardActionListener)
                .withActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME))
                .withShortcut('X')
                .build(this);

        MenuItemBuilder.ofAction(new DefaultEditorKit.CopyAction())
                .named("Copy")
                .withAction(canvasActionListener)
                .withAction(cardActionListener)
                .withActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME))
                .withShortcut('C')
                .build(this);

        MenuItemBuilder.ofAction(new DefaultEditorKit.PasteAction())
                .named("Paste")
                .withAction(canvasActionListener)
                .withAction(cardActionListener)
                .withActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME))
                .withShortcut('V')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Clear")
                .withAction(e -> ((AbstractSelectionTool) ToolsContext.getInstance().getPaintTool()).deleteSelection())
                .withDisabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Objects::isNull))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Card")
                .withAction(e -> HyperCard.getInstance().getActiveStack().newCard())
                .withShortcut('N')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Card")
                .withDisabledProvider(HyperCard.getInstance().getActiveStackCardCountProvider().map(c -> c < 2))
                .withAction(e -> HyperCard.getInstance().getActiveStack().deleteCard())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Cut Card")
                .withDisabledProvider(HyperCard.getInstance().getActiveStackCardCountProvider().map(c -> c < 2))
                .withAction(e -> HyperCard.getInstance().getActiveStack().cutCard())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Copy Card")
                .withAction(e -> HyperCard.getInstance().getActiveStack().copyCard())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paste Card")
                .withEnabledProvider(HyperCard.getInstance().getActiveStackCardClipboardProvider().map(Optional::isPresent))
                .withAction(e -> HyperCard.getInstance().getActiveStack().pasteCard())
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
                .named("Create Icon...")
                .withEnabledProvider(ToolsContext.getInstance().getSelectedImageProvider().map(Optional::isPresent))
                .withAction(e -> WindowBuilder.make(new IconCreator())
                        .resizeable(false)
                        .withTitle("Create Icon")
                        .asModal()
                        .withModel(ToolsContext.getInstance().getSelectedImage())
                        .build())
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

        JMenuItem laf = MenuItemBuilder.ofHierarchicalType()
                .named("Look & Feel")
                .build(this);

        for (UIManager.LookAndFeelInfo thisLaf : UIManager.getInstalledLookAndFeels()) {

            MenuItemBuilder.ofCheckType()
                    .named(thisLaf.getName())
                    .withAction(a -> WindowManager.setLookAndFeel(thisLaf.getClassName()))
                    .withCheckmarkProvider(WindowManager.getLookAndFeelClassProvider().map(value -> thisLaf.getClassName().equals(value)))
                    .build(laf);
        }
    }

    public void reset() {
        instance = new EditMenu();
    }
}
