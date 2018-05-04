package com.defano.wyldcard;

import com.defano.hypertalk.exception.ExitToHyperCardException;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.KeyboardManager;
import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.cursor.CursorManager;
import com.defano.wyldcard.menubar.main.HyperCardMenuBar;
import com.defano.wyldcard.parts.editor.PartEditManager;
import com.defano.wyldcard.parts.finder.PartFinder;
import com.defano.wyldcard.patterns.PatternManager;
import com.defano.wyldcard.runtime.PeriodicMessageManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.FileContext;
import com.defano.wyldcard.runtime.context.PartToolContext;
import com.defano.wyldcard.window.WindowManager;
import com.defano.wyldcard.window.layouts.HyperTalkErrorDialog;

import javax.swing.*;

/**
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */
public class WyldCard extends StackManager implements PartFinder {

    private static WyldCard instance = new WyldCard();

    private WyldCard() {
    }

    public static WyldCard getInstance() {
        return instance;
    }

    public static void main(String argv[]) {

        try {
            // Configure macOS environment
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WyldCard");
            System.setProperty("apple.awt.application.name", "WyldCard");
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");

        } catch (Exception e) {
            e.printStackTrace();
        }

        getInstance().startup();
    }

    private void startup() {

        SwingUtilities.invokeLater(() -> {
            KeyboardManager.getInstance().start();              // Global key event handler
            MouseManager.getInstance().start();                 // Global mouse event and mouseLoc handler
            PartEditManager.getInstance().start();              // Button field movement and resize management
            WindowManager.getInstance().start();                // Window and palette management
            PatternManager.getInstance().start();               // Update pattern palette on color changes
            PeriodicMessageManager.getInstance().start();       // Idle and mouseWithin periodic message generation
            CursorManager.getInstance().start();                // Mouse cursor assignment
            PartToolContext.getInstance().start();              // Button and field tool selection state

            super.newStack(new ExecutionContext());

            // Need to have an open stack before showing the menu bar
            SwingUtilities.invokeLater(() -> HyperCardMenuBar.getInstance().reset());
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileContext.getInstance().closeAll()));
    }

    /**
     * Display a syntax error dialog containing, when a breadcrumb is available, an "edit script" button that launches
     * a script editor with the offending line highlighted.
     *
     * @param e
     */
    public void showErrorDialog(HtException e) {
        SwingUtilities.invokeLater(() -> HyperTalkErrorDialog.getInstance().showError(e));

        // Abort further script execution
        throw new ExitToHyperCardException();
    }

}
