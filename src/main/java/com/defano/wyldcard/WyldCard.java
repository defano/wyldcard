package com.defano.wyldcard;

import com.defano.hypertalk.exception.ExitToHyperCardException;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.awt.KeyboardManager;
import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.cursor.CursorManager;
import com.defano.wyldcard.parts.editor.PartEditManager;
import com.defano.wyldcard.parts.finder.PartFinder;
import com.defano.wyldcard.parts.stack.StackPart;
import com.defano.wyldcard.patterns.PatternManager;
import com.defano.wyldcard.runtime.PeriodicMessageManager;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.runtime.context.FileContext;
import com.defano.wyldcard.window.layouts.HyperTalkErrorDialog;
import com.defano.wyldcard.window.WindowManager;

import javax.swing.*;
import java.util.Arrays;

/**
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */
public class WyldCard extends StackManager implements PartFinder {

    private static WyldCard instance = new WyldCard();

    public static WyldCard getInstance() {
        return instance;
    }

    private WyldCard() {}

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
        StackPart stack = focusStack(StackPart.newStack(new ExecutionContext()));

        SwingUtilities.invokeLater(() -> {
            KeyboardManager.getInstance().start();              // Global key event handler
            MouseManager.getInstance().start();                 // Global mouse event and mouseLoc handler
            PartEditManager.getInstance().start();              // Button/tool selection and edit management
            WindowManager.getInstance().start();                // Window and palette management
            CursorManager.getInstance().start();                // Mouse cursor assignment
            PatternManager.getInstance().start();               // Update pattern palette on color changes
            PeriodicMessageManager.getInstance().start();       // Idle and mouseWithin periodic message generation

            stack.bindToWindow(WindowManager.getInstance().getWindowForStack(stack));
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileContext.getInstance().closeAll()));
    }

    /**
     * Display a syntax error dialog containing, when a breadcrumb is available, an "edit script" button that launches
     * a script editor with the offending line highlighted.
     * @param e
     */
    public void showErrorDialog(HtException e) {
        SwingUtilities.invokeLater(() -> HyperTalkErrorDialog.getInstance().showError(e));

        // Abort further script execution
        throw new ExitToHyperCardException();
    }

}
