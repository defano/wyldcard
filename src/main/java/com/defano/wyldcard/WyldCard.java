package com.defano.wyldcard;

import com.defano.wyldcard.aspect.RunOnDispatch;
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
import com.defano.wyldcard.window.HyperTalkErrorDialog;
import com.defano.wyldcard.window.WindowManager;
import com.defano.hypertalk.exception.ExitToHyperCardException;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.window.forms.BackgroundPropertyEditor;

import javax.swing.*;

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

    public static void main(String argv[]) {

        new BackgroundPropertyEditor();

        try {
            // Configure macOS environment
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WyldCard");
            System.setProperty("apple.awt.application.name", "HyperCard");
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        } catch (Exception e) {
            e.printStackTrace();
        }

        getInstance().startup();
    }

    private WyldCard() {}

    private void startup() {
        ExecutionContext context = new ExecutionContext();
        StackPart stack = StackPart.newStack(context);
        focusStack(stack);

        SwingUtilities.invokeLater(() -> {
            KeyboardManager.getInstance().start();
            MouseManager.getInstance().start();
            PartEditManager.getInstance().start();
            WindowManager.getInstance().start();
            CursorManager.getInstance().start();
            PatternManager.getInstance().start();
            PeriodicMessageManager.getInstance().start();

            stack.displayInWindow(context, WindowManager.getInstance().getStackWindow(stack));
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileContext.getInstance().closeAll()));
    }

    public void showErrorDialog(HtException e) {
        SwingUtilities.invokeLater(() -> HyperTalkErrorDialog.getInstance().showError(e));

        // Abort further script execution
        throw new ExitToHyperCardException();
    }

    @RunOnDispatch
    public void quit() {
        // Prompt to save if user has unsaved changes
        if (isActiveStackDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    WindowManager.getInstance().getFocusedStack().getDisplayedCard(),
                    "Save changes to stack?",
                    "Save",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.CLOSED_OPTION) {
                return;
            } else if (dialogResult == JOptionPane.YES_OPTION) {
                saveActiveStack(new ExecutionContext());
            }
        }

        System.exit(0);
    }

}
