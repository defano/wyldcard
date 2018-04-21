package com.defano.wyldcard.menu.main;

import com.defano.hypertalk.ast.model.Value;
import com.defano.jmonet.clipboard.CanvasClipboardActionListener;
import com.defano.jmonet.tools.base.AbstractSelectionTool;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menu.HyperCardMenu;
import com.defano.wyldcard.menu.MenuItemBuilder;
import com.defano.wyldcard.parts.clipboard.CardActionListener;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.FontContext;
import com.defano.wyldcard.runtime.context.ToolsContext;
import com.defano.wyldcard.window.WindowBuilder;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.forms.IconCreator;
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
        CanvasClipboardActionListener canvasActionListener = new CanvasClipboardActionListener(() -> WyldCard.getInstance().getActiveStackDisplayedCard().getCanvas());
        CardActionListener cardActionListener = new CardActionListener();

        MenuItemBuilder.ofDefaultType()
                .named("Undo")
                .withShortcut('Z')
                .withAction(e -> WyldCard.getInstance().getActiveStackDisplayedCard().getCanvas().undo())
                .withEnabledProvider(WyldCard.getInstance().getIsUndoableProvider())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Redo")
                .withShiftShortcut('Z')
                .withAction(e -> WyldCard.getInstance().getActiveStackDisplayedCard().getCanvas().redo())
                .withEnabledProvider(WyldCard.getInstance().getIsRedoableProvider())
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
                .withAction(e -> WindowManager.getInstance().getFocusedStack().newCard(new ExecutionContext()))
                .withShortcut('N')
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Delete Card")
                .withDisabledProvider(WyldCard.getInstance().getActiveStackCardCountProvider().map(c -> c < 2))
                .withAction(e -> WindowManager.getInstance().getFocusedStack().deleteCard(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Cut Card")
                .withDisabledProvider(WyldCard.getInstance().getActiveStackCardCountProvider().map(c -> c < 2))
                .withAction(e -> WindowManager.getInstance().getFocusedStack().cutCard(new ExecutionContext()))
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Copy Card")
                .withAction(e -> WindowManager.getInstance().getFocusedStack().copyCard())
                .build(this);

        MenuItemBuilder.ofDefaultType()
                .named("Paste Card")
                .withEnabledProvider(WyldCard.getInstance().getActiveStackCardClipboardProvider().map(Optional::isPresent))
                .withAction(e -> WindowManager.getInstance().getFocusedStack().pasteCard(new ExecutionContext()))
                .build(this);

        this.addSeparator();

        this.addSeparator();

        MenuItemBuilder.ofDefaultType()
                .named("Text Style...")
                .withCheckmarkProvider(FontContext.getInstance().getFocusedFontSizeProvider().map(e -> !e.contains(new Value(9)) && !e.contains(new Value(10)) && !e.contains(new Value(12)) && !e.contains(new Value(14)) && !e.contains(new Value(18)) && !e.contains(new Value(24))))
                .withAction(e -> FontContext.getInstance().setSelectedFont(JFontChooser.showDialog(WindowManager.getInstance().getFocusedStackWindow(), "Choose Font", FontContext.getInstance().getFocusedTextStyle().toFont())))
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
                    .withAction(a -> WindowManager.getInstance().setLookAndFeel(thisLaf.getClassName()))
                    .withCheckmarkProvider(WindowManager.getInstance().getLookAndFeelClassProvider().map(value -> thisLaf.getClassName().equals(value)))
                    .build(laf);
        }
    }

    public void reset() {
        instance = new EditMenu();
    }
}
