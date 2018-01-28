package com.defano.hypercard;

import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypercard.awt.MouseManager;
import com.defano.hypercard.cursor.CursorManager;
import com.defano.hypercard.parts.editor.PartEditor;
import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.runtime.PeriodicMessageManager;
import com.defano.hypercard.runtime.context.FileContext;
import com.defano.hypercard.window.HyperTalkErrorDialog;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.exception.HtException;

import javax.swing.*;

/**
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */
public class HyperCard extends StackManager {

    private static HyperCard instance;

    public static void main(String argv[]) {
        try {
            // Display the frame's menu as the Mac OS menubar
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HyperCard");
            System.setProperty("apple.awt.application.name", "HyperCard");
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        } catch (Exception e) {
            e.printStackTrace();
        }

        instance = new HyperCard();
    }

    private HyperCard() {

        // Fire up the key and mouse listeners
        KeyboardManager.start();
        MouseManager.start();
        PartEditor.start();

        SwingUtilities.invokeLater(() -> {
            WindowManager.start();
            CursorManager.getInstance().start();
            PeriodicMessageManager.getInstance().start();

            setActiveStack(StackPart.newStack());
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileContext.getInstance().closeAll()));
    }

    public static HyperCard getInstance() {
        return instance;
    }

    public void showErrorDialog(HtException e) {
        HyperTalkErrorDialog.getInstance().showError(e);
    }

    public void quit() {

        // Prompt to save if user has unsaved changes
        if (isActiveStackDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(getActiveStack().getDisplayedCard(), "Save changes to stack?", "Save", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                save(getActiveStack().getStackModel());
            }
        }

        System.exit(0);
    }

}
