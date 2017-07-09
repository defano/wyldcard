/*
 * HyperCard
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypercard.gui.util.MouseManager;
import com.defano.hypercard.gui.util.KeyboardManager;
import com.defano.hypercard.parts.StackPart;
import com.defano.hypercard.parts.CardPart;
import com.defano.hypercard.parts.model.StackModel;
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
    private static ExecutorService messageBoxExecutor = Executors.newSingleThreadExecutor();
    private StackPart stackPart = StackPart.getInstance();

    public static void main(String argv[]) {
        // Display the frame's menu as the Mac OS menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HyperCard");
        System.setProperty("apple.awt.application.name", "HyperCard");

        instance = new HyperCard();
    }

    private HyperCard() {

        try {
            // Use this operating systems look and feel for our user interface.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Nothing to do
        }

        // Fire up the key and mouse listeners
        KeyboardManager.start();
        MouseManager.start();

        // Window manager expects this object to be fully initialized before it can start, thus, we can't invoke
        // directly from the constructor. This behaves like @PostConstruct
        SwingUtilities.invokeLater(() -> {
            WindowManager.start();
            openStack(StackModel.newStackModel("Untitled"));
        });
    }

    public static HyperCard getInstance() {
        return instance;
    }

    public StackPart getStack() {
        return stackPart;
    }

    public void openStack(StackModel model) {
        stackPart.open(model);
        stackPart.goCard(stackPart.getStackModel().getCurrentCardIndex());
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
                        HyperCard.getInstance().setMessageBoxText(GlobalContext.getContext().getIt());
                    }
                }
            } catch (Exception e) {
                HyperCard.getInstance().showErrorDialog(e);
            }
        });
    }

    public void showErrorDialog(Exception e) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(WindowManager.getStackWindow().getWindowPanel(), e.getMessage()));
    }
}
