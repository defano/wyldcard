/*
 * ScriptEditor
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.window;

import com.defano.hypercard.parts.model.PartModel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.defano.hypercard.gui.HyperCardFrame;
import com.defano.hypercard.gui.util.SquigglePainter;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSyntaxException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import java.awt.*;

public class ScriptEditor extends HyperCardFrame {

    private final static Highlighter.HighlightPainter ERROR_HIGHLIGHTER = new SquigglePainter(Color.RED);
    private final static String DEFAULT_SCRIPT = new StringBuilder()
            .append("on mouseUp\n")
            .append("    -- Fires whenever the mouse is clicked and released over this part.\n")
            .append("end mouseUp\n")
            .append("\n")
            .append("on mouseDown\n")
            .append("    -- Fires whenever the mouse is clicked over this part.\n")
            .append("end mouseDown\n")
            .append("\n")
            .append("on mouseDoubleClick\n")
            .append("    -- Fires whenever the mouse is double-clicked over this part.\n")
            .append("end mouseDoubleClick\n")
            .append("\n")
            .append("on mouseEnter\n")
            .append("    -- Fires whenever the mouse is moved over this part.\n")
            .append("end mouseEnter\n")
            .append("\n")
            .append("on mouseLeave\n")
            .append("    -- Fires whenever the mouse is moved away from this part.\n")
            .append("end mouseLeave\n")
            .toString();

    private PartModel model;

    private JPanel scriptEditor;
    private JButton cancelButton;
    private JButton saveButton;
    private JTextArea scriptField;

    public ScriptEditor() {

        cancelButton.addActionListener(e -> dispose());

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
    }

    private void checkSyntax() {
        try {
            Interpreter.compile(scriptField.getText());
            scriptField.getHighlighter().removeAllHighlights();
        } catch (HtSyntaxException e1) {
            setHighlightedLine(e1.lineNumber - 1);
        } catch (HtException e1) {
            setHilightedLine();
        }
    }

    private void setHilightedLine() {
        try {
            setHighlightedLine(scriptField.getLineOfOffset(scriptField.getCaretPosition()));
        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    private void setHilightedSelection(int start, int end) {
        try {
            scriptField.getHighlighter().addHighlight(start, end, ERROR_HIGHLIGHTER);
        } catch (BadLocationException e) {
            // Nothing to do
        }
    }

    private void setHighlightedLine(int line) {
        try {
            setHilightedSelection(scriptField.getLineStartOffset(line), scriptField.getLineEndOffset(line));
        } catch (BadLocationException e) {
            // Nothing to do
        }
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
            scriptField.setText(script.trim().isEmpty() ? DEFAULT_SCRIPT : script);
        } else {
            throw new RuntimeException("Bug! Don't know how to bind data class to window." + properties);
        }
    }

    private void updateProperties() {
        model.setKnownProperty("script", new Value(scriptField.getText()));
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
        scriptEditor.setLayout(new GridLayoutManager(2, 4, new Insets(10, 10, 10, 10), 0, -1));
        scriptEditor.setPreferredSize(new Dimension(640, 480));
        saveButton = new JButton();
        saveButton.setHideActionText(false);
        saveButton.setHorizontalAlignment(4);
        saveButton.setOpaque(true);
        saveButton.setText("Save");
        scriptEditor.add(saveButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        scriptEditor.add(spacer1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        Font scrollPane1Font = this.$$$getFont$$$("Monaco", -1, -1, scrollPane1.getFont());
        if (scrollPane1Font != null) scrollPane1.setFont(scrollPane1Font);
        scriptEditor.add(scrollPane1, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        scriptField = new JTextArea();
        Font scriptFieldFont = this.$$$getFont$$$("Monaco", -1, -1, scriptField.getFont());
        if (scriptFieldFont != null) scriptField.setFont(scriptFieldFont);
        scriptField.setTabSize(4);
        scrollPane1.setViewportView(scriptField);
        cancelButton = new JButton();
        cancelButton.setHorizontalAlignment(4);
        cancelButton.setText("Cancel");
        scriptEditor.add(cancelButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return scriptEditor;
    }
}
