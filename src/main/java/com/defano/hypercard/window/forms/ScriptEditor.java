/*
 * ScriptEditor
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.window.forms;

import com.defano.hypercard.util.HandlerComboBox;
import com.defano.hypercard.window.HyperCardFrame;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypertalk.ast.common.Script;
import com.defano.hypertalk.ast.common.SystemMessage;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.defano.hypercard.util.SquigglePainter;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtSyntaxException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.Utilities;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ScriptEditor extends HyperCardFrame implements HandlerComboBox.HandlerComboBoxDelegate {

    private final static Highlighter.HighlightPainter ERROR_HIGHLIGHTER = new SquigglePainter(Color.RED);
    private PartModel model;
    private Script compiledScript;

    private JPanel scriptEditor;
    private JButton saveButton;
    private JTextArea scriptField;
    private HandlerComboBox handlersMenu;
    private HandlerComboBox functionsMenu;
    private JLabel charCount;

    public ScriptEditor() {

        handlersMenu.setDelegate(this);
        functionsMenu.setDelegate(this);

        saveButton.addActionListener(e -> {
            updateProperties();
            dispose();
        });

        scriptField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                checkSyntax();
            }

            public void removeUpdate(DocumentEvent e) {
                checkSyntax();
            }

            public void insertUpdate(DocumentEvent e) {
                checkSyntax();
            }
        });

        scriptField.addCaretListener(e -> updateActiveHandler());
    }

    private void checkSyntax() {
        charCount.setText(String.valueOf(scriptField.getText().length()) + " characters, " + String.valueOf(scriptField.getText().split("\n").length) + " lines.");
        Interpreter.compileInBackground(scriptField.getText(), (scriptText, compiledScript, generatedError) -> {
            if (compiledScript != null) {
                ScriptEditor.this.compiledScript = compiledScript;
            }
            scriptField.getHighlighter().removeAllHighlights();
            handlersMenu.invalidateDataset();
            functionsMenu.invalidateDataset();

            if (generatedError instanceof HtSyntaxException) {
                setHighlightedLine(((HtSyntaxException) generatedError).lineNumber - 1);
            } else if (generatedError != null) {
                setHighlightedLine();
            }
        });
    }

    private void setHighlightedLine() {
        try {
            setHighlightedLine(scriptField.getLineOfOffset(scriptField.getCaretPosition()));
        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    private void setHighlightedSelection(int start, int end) {
        try {
            scriptField.getHighlighter().addHighlight(start, end, ERROR_HIGHLIGHTER);
        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    private void setHighlightedLine(int line) {
        try {
            setHighlightedSelection(scriptField.getLineStartOffset(line), scriptField.getLineEndOffset(line));
        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    @Override
    public JButton getDefaultButton() {
        return saveButton;
    }

    @Override
    public JPanel getWindowPanel() {
        return scriptEditor;
    }

    @Override
    public void bindModel(Object properties) {
        if (properties instanceof PartModel) {
            this.model = (PartModel) properties;
            String script = this.model.getKnownProperty("script").stringValue();
            scriptField.setText(script.trim());
            checkSyntax();
        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window: " + properties);
        }
    }

    private void updateProperties() {
        model.setKnownProperty("script", new Value(scriptField.getText()));
    }

    @Override
    public Collection<String> getImplementedHandlers(HandlerComboBox theComboBox) {
        if (theComboBox == functionsMenu) {
            return compiledScript == null ? new ArrayList<>() : compiledScript.getFunctions();
        } else {
            return compiledScript == null ? new ArrayList<>() : compiledScript.getHandlers();
        }
    }

    @Override
    public Collection<String> getSystemMessages(HandlerComboBox theComboBox) {
        ArrayList<String> messages = new ArrayList<>();
        if (theComboBox == functionsMenu) {
            return Collections.singletonList("New function...");
        } else {
            if (model != null) {
                for (SystemMessage message : SystemMessage.messagesSentTo(model.getType())) {
                    messages.add(message.messageName);
                }
            }
        }
        return messages;
    }

    @Override
    public void jumpToHandler(HandlerComboBox theComboBox, String handler) {

        // Script is empty, add selected handler
        if (compiledScript == null) {
            if (theComboBox == functionsMenu) {
                appendFunctionTemplate();
            } else {
                appendHandler(handler);
            }
        }

        // Script is not empty; see if the selected handler exists
        else {
            Integer lineNumber = compiledScript.getLineNumberForNamedBlock(handler);
            if (lineNumber == null) {
                if (theComboBox == functionsMenu) {
                    appendFunctionTemplate();
                } else {
                    appendHandler(handler);
                }
            } else {
                scriptField.requestFocus();
                jumpToLine(lineNumber);
            }
        }
    }

    private void updateActiveHandler() {
        if (compiledScript != null) {
            handlersMenu.setActiveHandler(compiledScript.getNamedBlockForLine(currentLine()));
            functionsMenu.setActiveHandler(compiledScript.getNamedBlockForLine(currentLine()));
        }
    }

    private void appendFunctionTemplate() {
        appendNamedBlock("function", null, "myFunction", new String[]{"arg1", "arg2"});
    }

    /**
     * Appends the handler to the script, including, when available, a description and argument list.
     *
     * @param handlerName The name of the handler to append.
     */
    private void appendHandler(String handlerName) {
        SystemMessage message = SystemMessage.fromHandlerName(handlerName);
        appendNamedBlock("on", message.description, message.messageName, message.arguments);
    }

    private void appendNamedBlock(String blockOpener, String description, String blockName, String[] arguments) {
        int lastIndex = scriptField.getDocument().getLength();
        StringBuilder builder = new StringBuilder();

        if (scriptField.getText().length() > 0 && !scriptField.getText().endsWith("\n")) {
            builder.append("\n\n");
        }

        // Add the handler description if one exists
        if (description != null) {
            builder.append("--\n-- ");
            builder.append(description);
            builder.append("\n--");
        }

        // Add the 'on handler' text
        builder.append("\n");
        builder.append(blockOpener);
        builder.append(" ");
        builder.append(blockName);

        // Add the handler arguments, if they exist
        if (arguments != null) {
            builder.append(" ");
            for (int index = 0; index < arguments.length; index++) {
                builder.append(arguments[index]);
                if (index < arguments.length - 1) {
                    builder.append(", ");
                }
            }
        }

        // Add the 'end handler' text
        builder.append("\n\nend ");
        builder.append(blockName);

        try {
            scriptField.getDocument().insertString(lastIndex, builder.toString(), null);
        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    private void jumpToLine(int lineIndex) {
        scriptField.setCaretPosition(scriptField.getDocument().getDefaultRootElement().getElement(lineIndex).getStartOffset());
    }

    private int currentLine() {
        int caretPos = scriptField.getCaretPosition();
        int rowNum = (caretPos == 0) ? 1 : 0;
        for (int offset = caretPos; offset > 0; ) {
            try {
                offset = Utilities.getRowStart(scriptField, offset) - 1;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            rowNum++;
        }
        return rowNum;
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
        scriptEditor = new JPanel();
        scriptEditor.setLayout(new GridLayoutManager(4, 8, new Insets(10, 10, 10, 10), 0, -1));
        scriptEditor.setPreferredSize(new Dimension(640, 480));
        saveButton = new JButton();
        saveButton.setHideActionText(false);
        saveButton.setOpaque(true);
        saveButton.setText("Save");
        scriptEditor.add(saveButton, new GridConstraints(3, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        Font scrollPane1Font = this.$$$getFont$$$("Monaco", -1, -1, scrollPane1.getFont());
        if (scrollPane1Font != null) scrollPane1.setFont(scrollPane1Font);
        scriptEditor.add(scrollPane1, new GridConstraints(2, 0, 1, 8, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scriptField = new JTextArea();
        Font scriptFieldFont = this.$$$getFont$$$("Monaco", -1, -1, scriptField.getFont());
        if (scriptFieldFont != null) scriptField.setFont(scriptFieldFont);
        scriptField.setTabSize(4);
        scrollPane1.setViewportView(scriptField);
        handlersMenu = new com.defano.hypercard.util.HandlerComboBox();
        scriptEditor.add(handlersMenu, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Handlers: ");
        scriptEditor.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Functions: ");
        scriptEditor.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        scriptEditor.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        functionsMenu = new com.defano.hypercard.util.HandlerComboBox();
        scriptEditor.add(functionsMenu, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        scriptEditor.add(spacer2, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        charCount = new JLabel();
        charCount.setText("Label");
        scriptEditor.add(charCount, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        scriptEditor.add(spacer3, new GridConstraints(3, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return scriptEditor;
    }
}
