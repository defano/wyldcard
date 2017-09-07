/*
 * HyperCard
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypercard.context.FileContext;
import com.defano.hypercard.gui.util.KeyboardManager;
import com.defano.hypercard.gui.util.MouseManager;
import com.defano.hypercard.parts.editor.PartEditor;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.stack.StackModel;
import com.defano.hypercard.parts.stack.StackPart;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.runtime.WindowManager;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */
public class HyperCard {

    private static HyperCard instance;
    private static final ExecutorService messageBoxExecutor = Executors.newSingleThreadExecutor();
    private final StackPart stackPart;

    public static void main(String argv[]) {
        try {
            // Display the frame's menu as the Mac OS menubar
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HyperCard");
            System.setProperty("apple.awt.application.name", "HyperCard");
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
        });

        // Close all open files before we die
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileContext.getInstance().closeAll()));
    }

    public static HyperCard getInstance() {
        return instance;
    }

    public StackPart getStack() {
        return stackPart;
    }

    public void openStack(StackModel model) {
        stackPart.open(model);
        stackPart.goCard(stackPart.getStackModel().getCurrentCardIndex(), null);
    }

    public CardPart getCard() {
        return stackPart.getCurrentCard();
    }

    public void setMessageBoxText(Object theMsg) {
        SwingUtilities.invokeLater(() -> WindowManager.getMessageWindow().setMsgBoxText(theMsg.toString()));
    }

    public String getMessageBoxText() {
        return WindowManager.getMessageWindow().getMsgBoxText();
    }

    public void evaluateMessageBox() {
        messageBoxExecutor.submit(() -> {
            try {
                if (!getMessageBoxText().trim().isEmpty()) {
                    String messageText = getMessageBoxText();
                    Interpreter.executeString(null, messageText).get();

                    // Replace the message box text with the result of evaluating the expression (ignore if user entered statement)
                    if (Interpreter.isExpressionStatement(messageText)) {
                        HyperCard.getInstance().setMessageBoxText(ExecutionContext.getContext().getIt());
                    }
                }
            } catch (Exception e) {
                HyperCard.getInstance().showErrorDialog(e);
            }
        });
    }

    public void showErrorDialog(Exception e) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(WindowManager.getStackWindow().getWindowPanel(), e.getMessage()));
        e.printStackTrace();
    }
}
