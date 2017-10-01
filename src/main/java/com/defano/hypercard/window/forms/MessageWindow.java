/*
 * MessageWindow
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.window.forms;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.field.ManagedSelection;
import com.defano.hypercard.parts.model.PropertyChangeObserver;
import com.defano.hypercard.parts.msgbox.MsgBoxModel;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.util.SquigglePainter;
import com.defano.hypercard.window.HyperCardFrame;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.containers.PartMessageSpecifier;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MessageWindow extends HyperCardFrame implements ManagedSelection, PropertyChangeObserver {

    private final static Highlighter.HighlightPainter ERROR_HIGHLIGHTER = new SquigglePainter(Color.RED);

    private MsgBoxModel partModel;
    private JTextField messageBox;
    private JPanel messageWindow;
    private final ArrayList<String> messageStack = new ArrayList<>();
    private int messageStackIndex = -1;

    public MessageWindow() {

        // Handle syntax checking and message execution key typed events
        messageBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    messageStack.add(messageBox.getText());
                    messageStackIndex = messageStack.size();

                    evaluateMessageBox();
                } else {
                    SwingUtilities.invokeLater(() -> checkSyntax());
                }
            }
        });

        // Handle message stack key press events
        messageBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (messageStackIndex > 0) {
                            messageBox.setText(messageStack.get(--messageStackIndex));
                        }
                        break;

                    case KeyEvent.VK_DOWN:
                        if (messageStackIndex < messageStack.size() - 1) {
                            messageBox.setText(messageStack.get(++messageStackIndex));
                        }
                        break;
                }

                SwingUtilities.invokeLater(() -> checkSyntax());
            }
        });

        // Update selection
        messageBox.addCaretListener(e -> MessageWindow.this.updateSelectionContext());

        SwingUtilities.invokeLater(() -> {
            partModel = new MsgBoxModel();
            partModel.addPropertyChangedObserver(MessageWindow.this);
        });
    }

    @Override
    public String getHyperTalkAddress() {
        return "the message";
    }

    @Override
    public PartSpecifier getPartSpecifier() {
        return new PartMessageSpecifier();
    }

    private void checkSyntax() {
        try {
            Interpreter.compile(messageBox.getText());
            messageBox.getHighlighter().removeAllHighlights();
        } catch (HtException e) {
            squiggleHighlight();
        }
    }

    private void squiggleHighlight() {
        try {
            messageBox.getHighlighter().addHighlight(0, messageBox.getText().length(), ERROR_HIGHLIGHTER);
        } catch (BadLocationException e1) {
            // Nothing to do
        }
    }

    @Override
    public JPanel getWindowPanel() {
        return messageWindow;
    }

    @Override
    public void bindModel(Object data) {
        // Nothing to do
    }

    public JTextComponent getTextComponent() {
        return messageBox;
    }

    public MsgBoxModel getPartModel() {
        return partModel;
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {
        switch (property) {
            case MsgBoxModel.PROP_CONTENTS:
                getTextComponent().setText(newValue.stringValue());
                break;
        }
    }

    public void setMsgBoxText(String text) {
        partModel.setKnownProperty(MsgBoxModel.PROP_CONTENTS, new Value(text));
    }

    public String getMsgBoxText() {
        return getTextComponent().getText();
    }

    public void evaluateMessageBox() {
        Interpreter.getMessageExecutor().execute(() -> {
            try {
                if (!getMsgBoxText().trim().isEmpty()) {
                    String messageText = getMsgBoxText();
                    Interpreter.executeString(new PartMessageSpecifier(), messageText).get();

                    // Replace the message box text with the result of evaluating the expression (ignore if user entered statement)
                    if (Interpreter.isExpressionStatement(messageText)) {
                        setMsgBoxText(ExecutionContext.getContext().getIt().stringValue());
                    }
                }
            } catch (Exception e) {
                HyperCard.getInstance().showErrorDialog(e);
            }
        });
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        messageWindow = new JPanel();
        messageWindow.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        messageWindow.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        messageBox = new JTextField();
        Font messageBoxFont = this.$$$getFont$$$("Monaco", -1, -1, messageBox.getFont());
        if (messageBoxFont != null) messageBox.setFont(messageBoxFont);
        messageWindow.add(messageBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(600, 25), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return messageWindow;
    }
}
