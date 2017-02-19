/*
 * HyperCard
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * HyperCard.java
 * @author matt.defano@gmail.com
 * 
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */

package com.defano.hypercard;

import com.defano.hypercard.context.GlobalContext;
import com.defano.hypercard.gui.util.MouseManager;
import com.defano.hypercard.context.ToolsContext;
import com.defano.hypercard.gui.util.KeyboardManager;
import com.defano.jmonet.model.PaintToolType;
import com.defano.hypercard.parts.CardPart;
import com.defano.hypercard.parts.model.StackModel;
import com.defano.hypercard.parts.model.StackModelObserver;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypercard.runtime.WindowManager;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HyperCard implements StackModelObserver {

    private static HyperCard instance;
    private static ExecutorService messageBoxExecutor = Executors.newSingleThreadExecutor();

    private StackModel stack;

    public static void main(String argv[]) {
        // Display the frame's menu as the Mac OS menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true" );
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

        // Create a new stack to work on.
        stack = StackModel.newStack("Untitled");
        stack.addObserver(this);

        // Fire up the key and mouse listeners
        KeyboardManager.start();
        MouseManager.start();

        // Window manager expects this object to be fully initialized before it can start, thus, we can't invoke
        // directly from the constructor
        SwingUtilities.invokeLater(() -> {
            WindowManager.start();
            ToolsContext.getInstance().selectPaintTool(PaintToolType.ARROW);
        });
    }

    public static HyperCard getInstance() {
        return instance;
    }

    public StackModel getStack () { return stack; }

    public void setStack (StackModel model) {
        SwingUtilities.invokeLater(() -> {
            stack = model;
            stack.addObserver(HyperCard.this);
            WindowManager.getStackWindow().setDisplayedCard(stack.getCurrentCard());
        });
    }

    public CardPart getCard () {
        return stack.getCurrentCard();
    }

    public void setMsgBoxText(Object theMsg) {
        SwingUtilities.invokeLater(() -> WindowManager.getMessageWindow().setMsgBoxText(theMsg.toString()));
    }

    public String getMsgBoxText() {
        return WindowManager.getMessageWindow().getMsgBoxText();
    }

    public void doMsgBoxText() {
        messageBoxExecutor.submit(() -> {
            try {
                if (!getMsgBoxText().trim().isEmpty()) {
                    String messageText = getMsgBoxText();
                    Interpreter.executeString(null, messageText).get();

                    // Replace the message box text with the result of evaluating the expression (ignore if user entered statement)
                    if (Interpreter.isExpressionStatement(messageText)) {
                        HyperCard.getInstance().setMsgBoxText(GlobalContext.getContext().getIt());
                    }
                }
            } catch (Exception e) {
                HyperCard.getInstance().dialogSyntaxError(e);
            }
        });
    }

    public void dialogSyntaxError(Exception e) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(WindowManager.getStackWindow().getWindowPanel(), e.getMessage()));
        e.printStackTrace();
    }

    @Override
    public void onCardClosing(CardPart oldCard) {
        // Nothing to do
    }

    @Override
    public void onCardOpening(CardPart newCard) {
        WindowManager.getStackWindow().setDisplayedCard(newCard);
    }

    @Override
    public void onCardOpened(CardPart newCard) {
        // Nothing to do
    }
}
