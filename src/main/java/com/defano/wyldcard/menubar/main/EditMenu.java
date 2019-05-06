package com.defano.wyldcard.menubar.main;

import com.defano.hypertalk.ast.model.Value;
import com.defano.jmonet.clipboard.CanvasClipboardActionListener;
import com.defano.jmonet.tools.base.SelectionTool;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.HyperCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.parts.clipboard.CardActionListener;
import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.layouts.IconCreator;
import com.l2fprod.common.swing.JFontChooser;

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
        CanvasClipboardActionListener canvasActionListener = new CanvasClipboardActionListener();
        CardActionListener cardActionListener = new CardActionListener();

        MenuItemBuilder.ofDefaultType()
                .named("Undo")
                .withShortcut('Z')
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedCard().getActiveCanvas().undo())
                .withEnabledProvider(WyldCard.getInstance().getStackManager().getIsUndoableProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Redo")
                .withShiftShortcut('Z')
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedCard().getActiveCanvas().redo())
                .withEnabledProvider(WyldCard.getInstance().getStackManager().getIsRedoableProvider())
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofAction(new DefaultEditorKit.CutAction())
                .named("Cut")
                .withDoMenuAction(canvasActionListener)
                .withDoMenuAction(cardActionListener)
                .withActionCommand((String) TransferHandler.getCutAction().getValue(Action.NAME))
                .withShortcut('X')
                .build(this);

        MenuItemBuilder.ofAction(new DefaultEditorKit.CopyAction())
                .named("Copy")
                .withDoMenuAction(canvasActionListener)
                .withDoMenuAction(cardActionListener)
                .withActionCommand((String) TransferHandler.getCopyAction().getValue(Action.NAME))
                .withShortcut('C')
                .build(this);

        MenuItemBuilder.ofAction(new DefaultEditorKit.PasteAction())
                .named("Paste")
                .withDoMenuAction(canvasActionListener)
                .withDoMenuAction(cardActionListener)
                .withActionCommand((String) TransferHandler.getPasteAction().getValue(Action.NAME))
                .withShortcut('V')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Clear")
                .withDoMenuAction(e -> ((SelectionTool) WyldCard.getInstance().getToolsManager().getPaintTool()).deleteSelection())
                .withDisabledProvider(WyldCard.getInstance().getToolsManager().getSelectedImageProvider().map(Objects::isNull))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("New Card")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedStack().newCard(new ExecutionContext()))
                .withShortcut('N')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Card")
                .withDisabledProvider(WyldCard.getInstance().getStackManager().getFocusedStackCardCountProvider().map(c -> c < 2))
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedStack().deleteCard(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Cut Card")
                .withDisabledProvider(WyldCard.getInstance().getStackManager().getFocusedStackCardCountProvider().map(c -> c < 2))
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedStack().cutCard(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Copy Card")
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedStack().copyCard())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paste Card")
                .withEnabledProvider(WyldCard.getInstance().getStackManager().getFocusedStackCardClipboardProvider().map(Optional::isPresent))
                .withDoMenuAction(e -> WyldCard.getInstance().getStackManager().getFocusedStack().pasteCard(new ExecutionContext()))
                .build(this);

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Text Style...")
                .withCheckmarkProvider(WyldCard.getInstance().getFontManager().getFocusedFontSizeProvider().map(e -> !e.contains(new Value(9)) && !e.contains(new Value(10)) && !e.contains(new Value(12)) && !e.contains(new Value(14)) && !e.contains(new Value(18)) && !e.contains(new Value(24))))
                .withDoMenuAction(e -> WyldCard.getInstance().getFontManager().setSelectedFont(JFontChooser.showDialog(WyldCard.getInstance().getWindowManager().getFocusedStackWindow(), "Choose Font", WyldCard.getInstance().getFontManager().getFocusedTextStyle().toFont())))
                .withShortcut('T')
                .build(this);

        MenuItemBuilder.ofCheckType()
                .named("Background")
                .withCheckmarkProvider(WyldCard.getInstance().getToolsManager().isEditingBackgroundProvider())
                .withDoMenuAction(e -> WyldCard.getInstance().getToolsManager().toggleIsEditingBackground())
                .withShortcut('B')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Create Icon...")
                .withEnabledProvider(WyldCard.getInstance().getToolsManager().getSelectedImageProvider().map(Optional::isPresent))
                .withDoMenuAction(e -> new WindowBuilder<>(new IconCreator())
                        .withModel(WyldCard.getInstance().getToolsManager().getSelectedImage())
                        .resizeable(false)
                        .withTitle("Create Icon")
                        .asModal()
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
                .named("Theme")
                .build(this);

        for (UIManager.LookAndFeelInfo thisLaf : UIManager.getInstalledLookAndFeels()) {

            MenuItemBuilder.ofCheckType()
                    .named(thisLaf.getName())
                    .withDoMenuAction(a -> WyldCard.getInstance().getWindowManager().setTheme(thisLaf.getClassName()))
                    .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getThemeProvider().map(value -> thisLaf.getName().equalsIgnoreCase(value)))
                    .build(laf);
        }
    }

    public void reset() {
        instance = new EditMenu();
    }
}
