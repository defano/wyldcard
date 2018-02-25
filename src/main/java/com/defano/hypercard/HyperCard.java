package com.defano.hypercard;

import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypercard.awt.MouseManager;
import com.defano.hypercard.cursor.CursorManager;
import com.defano.hypercard.parts.editor.PartManager;
import com.defano.hypercard.runtime.PeriodicMessageManager;
import com.defano.hypercard.runtime.context.FileContext;
import com.defano.hypercard.window.HyperTalkErrorDialog;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.exception.ExitToHyperCardException;
import com.defano.hypertalk.exception.HtException;

import javax.swing.*;

/**
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */
public class HyperCard extends StackManager {

    private static HyperCard instance;

    public static HyperCard getInstance() {
        return instance;
    }

    public static void main(String argv[]) {
        instance = new HyperCard();
    }

    private HyperCard() {

        try {
            // Configure macOS environment
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HyperCard");
            System.setProperty("apple.awt.application.name", "HyperCard");
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            KeyboardManager.getInstance().start();
            MouseManager.getInstance().start();
            PartManager.getInstance().start();
            WindowManager.getInstance().start();
            CursorManager.getInstance().start();
            PeriodicMessageManager.getInstance().start();

            newStack();
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileContext.getInstance().closeAll()));
    }

    public void showErrorDialog(HtException e) {
        HyperTalkErrorDialog.getInstance().showError(e);

        // Abort further script execution
        throw new ExitToHyperCardException();
    }

    public void quit() {

        // Prompt to save if user has unsaved changes
        if (isActiveStackDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    getActiveStack().getDisplayedCard(),
                    "Save changes to stack?",
                    "Save",
                    JOptionPane.YES_NO_OPTION);

            if (dialogResult == JOptionPane.CLOSED_OPTION) {
                return;
            } else if (dialogResult == JOptionPane.YES_OPTION) {
                saveActiveStack();
            }
        }

        System.exit(0);
    }

}
