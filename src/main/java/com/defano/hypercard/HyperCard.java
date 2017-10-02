/*
 * HyperCard
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard;

import com.defano.hypercard.runtime.PeriodicMessageManager;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.context.FileContext;
import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypercard.awt.MouseManager;
import com.defano.hypercard.parts.editor.PartEditor;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypercard.runtime.serializer.Serializer;
import com.defano.jmonet.model.Provider;

import javax.swing.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */
public class HyperCard {

    private static HyperCard instance;
    private final StackPart stackPart;
    private final Provider<File> savedStackFileProvider = new Provider<>();
    private AtomicBoolean errorDialogVisible = new AtomicBoolean(false);

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

        stackPart = StackPart.fromStackModel(StackModel.newStackModel("Untitled"));

        // Fire up the key and mouse listeners
        KeyboardManager.start();
        MouseManager.start();
        PartEditor.start();

        SwingUtilities.invokeLater(() -> {
            WindowManager.start();
            stackPart.open(stackPart.getStackModel());
            PeriodicMessageManager.getInstance().start();
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileContext.getInstance().closeAll()));
    }

    public static HyperCard getInstance() {
        return instance;
    }

    public Provider<File> getSavedStackFileProvider() {
        return savedStackFileProvider;
    }

    public void setSavedStackFile(File savedStackFileProvider) {
        this.savedStackFileProvider.set(savedStackFileProvider);
    }

    public StackPart getStack() {
        return stackPart;
    }

    public void openStack(StackModel model) {
        stackPart.open(model);
        stackPart.goCard(stackPart.getStackModel().getCurrentCardIndex(), null);
    }


    /**
     * Gets the card currently displayed in the stack window (no accounting for screen lock).
     *
     * Note that scripts should always use {@link ExecutionContext#getCurrentCard()} to retrieve a reference to the
     * current card, since, from the perspective of a script the active card may differ from the displayed card under
     * certain conditions.
     *
     * @return The card currently displayed in the stack window. 
     */
    public CardPart getDisplayedCard() {
        return stackPart.getDisplayedCard();
    }

    public void showErrorDialog(Exception e) {
        if (!errorDialogVisible.get()) {
            SwingUtilities.invokeLater(() -> {
                errorDialogVisible.set(true);
                JOptionPane.showMessageDialog(WindowManager.getStackWindow().getWindowPanel(), e.getMessage());
                errorDialogVisible.set(false);
            });
        }
        e.printStackTrace();
    }

    public void quit() {

        // Prompt to save if user has unsaved changes
        if (isDirty()) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Save changes to stack?", "Save", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                getStack().save(getSavedStackFileProvider().get());
            }
        }

        System.exit(0);
    }

    /**
     * A cheesy and expensive mechanism to determine if the user has made a change to the stack since it was last opened.
     * @return True if the stack has changes; false otherwise
     */
    private boolean isDirty() {
        try {
            String savedStack = new String(Files.readAllBytes(getSavedStackFileProvider().get().toPath()), StandardCharsets.UTF_8);
            String currentStack = Serializer.serialize(getStack().getStackModel());
            return !savedStack.equalsIgnoreCase(currentStack);
        } catch (Exception e) {
            return true;
        }
    }
}
