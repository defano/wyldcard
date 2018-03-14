package com.defano.wyldcard.window.forms;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.parts.model.PropertiesModel;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.parts.msgbox.MsgBoxModel;
import com.defano.wyldcard.runtime.interpreter.Interpreter;
import com.defano.wyldcard.runtime.interpreter.MessageEvaluationObserver;
import com.defano.wyldcard.util.SquigglePainter;
import com.defano.wyldcard.window.HyperCardFrame;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSyntaxException;
import com.defano.hypertalk.utils.Range;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MessageWindow extends HyperCardFrame implements PropertyChangeObserver {

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
        messageBox.addCaretListener(e -> getPartModel().updateSelectionContext(Range.ofMarkAndDot(e.getDot(), e.getMark()), getPartModel(), true));

        SwingUtilities.invokeLater(() -> {
            partModel = new MsgBoxModel();
            partModel.addPropertyChangedObserver(MessageWindow.this);
        });
    }

    @RunOnDispatch
    private void checkSyntax() {
        try {
            messageBox.getHighlighter().removeAllHighlights();
            Interpreter.blockingCompileScriptlet(messageBox.getText());
        } catch (HtException e) {
            squiggleHighlight(e);
        }
    }

    @RunOnDispatch
    private void squiggleHighlight(HtException e) {
        int squiggleStart = 0;
        int squiggleEnd = messageBox.getText().length();

        if (e instanceof HtSyntaxException) {
            Range offendingRange = e.getBreadcrumb().getCharRange();
            if (offendingRange != null) {
                squiggleStart = offendingRange.start;
                squiggleEnd = offendingRange.end;
            }
        }

        try {
            messageBox.getHighlighter().addHighlight(squiggleStart, squiggleEnd, ERROR_HIGHLIGHTER);
        } catch (BadLocationException e1) {
            throw new RuntimeException("Bug! Miscalculated text range.", e1);
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
    @RunOnDispatch
    public void onPropertyChanged(PropertiesModel model, String property, Value oldValue, Value newValue) {
        switch (property) {
            case MsgBoxModel.PROP_CONTENTS:
                getTextComponent().setText(newValue.stringValue());
                break;
        }
    }

    public void setMsgBoxText(String text) {
        partModel.setKnownProperty(MsgBoxModel.PROP_CONTENTS, new Value(text));
    }

    @RunOnDispatch
    public String getMsgBoxText() {
        return getTextComponent().getText();
    }

    /**
     * Show the message window, populate the field with a find command, and position the caret inside the query string.
     */
    @RunOnDispatch
    public void doFind() {
        setVisible(true);
        setMsgBoxText("find \"\"");
        getTextComponent().setCaretPosition(6);
    }

    private void evaluateMessageBox() {
        if (!getMsgBoxText().trim().isEmpty()) {
            String messageText = getMsgBoxText();
            Interpreter.asyncEvaluateMessage(messageText, new MessageEvaluationObserver() {
                @Override
                public void onMessageEvaluated(String result) {
                    // Replace the message box text with the result of evaluating the expression (ignore if user entered statement)
                    if (result != null) {
                        setMsgBoxText(result);
                    }
                }

                @Override
                public void onEvaluationError(HtException exception) {
                    WyldCard.getInstance().showErrorDialog(exception);
                }
            });
        }
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

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return messageWindow;
    }
}
