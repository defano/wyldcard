package com.defano.wyldcard.menubar.main;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.menubar.WyldCardMenu;
import com.defano.wyldcard.menubar.MenuItemBuilder;
import com.defano.wyldcard.window.WyldCardFrame;
import com.defano.wyldcard.window.layouts.FindWindow;
import com.defano.wyldcard.window.layouts.ReplaceWindow;
import com.defano.wyldcard.window.layouts.ScriptEditor;
import com.defano.wyldcard.window.layouts.StackWindow;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WindowsMenu extends WyldCardMenu {

    public static WindowsMenu instance = new WindowsMenu();

    public WindowsMenu() {
        super("Windows");
        WyldCard.getInstance().getWindowManager().getVisibleWindowsProvider().subscribe(wyldCardFrames -> reset());
    }

    public void reset() {
        SwingUtilities.invokeLater(() -> {
            WindowsMenu.super.removeAll();

            MenuItemBuilder.ofDefaultType()
                    .named("Minimize")
                    .withAction(a -> WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getWindow().setState(Frame.ICONIFIED))
                    .build(WindowsMenu.this);

            MenuItemBuilder.ofDefaultType()
                    .named("Zoom")
                    .withAction(a -> {
                        JFrame focusedFrame = WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getWindow();
                        focusedFrame.setExtendedState(focusedFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                    })
                    .build(WindowsMenu.this);

            MenuItemBuilder.ofDefaultType()
                    .named("Next Window")
                    .withShiftShortcut('.')
                    .withAction(a -> WyldCard.getInstance().getWindowManager().nextWindow().getWindow().requestFocus())
                    .build(WindowsMenu.this);

            MenuItemBuilder.ofDefaultType()
                    .named("Previous Window")
                    .withShiftShortcut(',')
                    .withAction(a -> WyldCard.getInstance().getWindowManager().prevWindow().getWindow().requestFocus())
                    .build(WindowsMenu.this);

            addSeparator();

            MenuItemBuilder.ofDefaultType()
                    .named("Restore Default Layout")
                    .withAction(a -> WyldCard.getInstance().getWindowManager().restoreDefaultLayout())
                    .build(WindowsMenu.this);

            MenuItemBuilder.ofDefaultType()
                    .named("Show All Tool Palettes")
                    .withAction(a -> WyldCard.getInstance().getWindowManager().showAllToolPalettes())
                    .build(WindowsMenu.this);

            addSeparator();

            addPalettes(MenuItemBuilder.ofHierarchicalType()
                    .named("Palettes")
                    .build(WindowsMenu.this));

            addScriptEditors(MenuItemBuilder.ofHierarchicalType()
                    .named("Script Editors")
                    .build(WindowsMenu.this));

            addSeparator();

            addStacks();
        });
    }

    private void addPalettes(JMenuItem parent) {
        MenuItemBuilder.ofCheckType()
                .named("Dock to Card Window")
                .withShiftShortcut('D')
                .withAction(a -> WyldCard.getInstance().getWindowManager().toggleDockPalettes())
                .withCheckmarkProvider(WyldCard.getInstance().getWindowManager().getPalettesDockedProvider())
                .build(parent);

        ((JMenu) parent).addSeparator();

        WyldCard.getInstance().getWindowManager().getPalettes(false)
                .stream()
                .filter(f -> !(f instanceof FindWindow) && !(f instanceof ReplaceWindow))
                .forEach(wyldCardFrame -> MenuItemBuilder.ofCheckType()
                        .named(wyldCardFrame.getTitle())
                        .withAction(a -> wyldCardFrame.toggleVisible())
                        .withCheckmarkProvider(wyldCardFrame.getWindowVisibleProvider())
                        .build(parent));
    }

    @SuppressWarnings("unchecked")
    private void addScriptEditors(JMenuItem parent) {
        List<WyldCardFrame> windows = WyldCard.getInstance().getWindowManager().getWindows(true);

        parent.setEnabled(windows.stream().anyMatch(p -> p instanceof ScriptEditor));

        windows.stream()
                .filter(w -> w instanceof ScriptEditor)
                .forEach(wyldCardFrame -> MenuItemBuilder.ofCheckType()
                        .named(wyldCardFrame.getTitle())
                        .withCheckmarkProvider(wyldCardFrame.getWindowFocusedProvider())
                        .withAction(a -> wyldCardFrame.getWindow().requestFocus())
                        .build(parent));
    }

    @SuppressWarnings("unchecked")
    private void addStacks() {
        WyldCard.getInstance().getWindowManager().getWindows(true)
                .stream()
                .filter(w -> w instanceof StackWindow)
                .forEach(wyldCardFrame -> MenuItemBuilder.ofCheckType()
                        .named(wyldCardFrame.getTitle())
                        .withCheckmarkProvider(wyldCardFrame.getWindowFocusedProvider())
                        .withAction(a -> wyldCardFrame.getWindow().requestFocus())
                        .build(WindowsMenu.this));
    }

}
