package com.defano.hypercard.parts.field;

import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.utils.Range;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public interface SelectableText {

    /**
     * Gets the JTextComponent containing the selectable text.
     * @return The component containing the selectable text.
     */
    JTextComponent getTextComponent();

    /**
     * Returns a HyperTalk expression that refers to this component, like 'card field id 1' or 'the message box'
     * @return A HyperTalk expression referring to this component
     */
    String getHyperTalkAddress();

    /**
     * Requests focus and sets a range of selected text in this field. If the start and end positions are equal, no
     * text is selected but the caret is moved to the given position.
     * @param start The selection start, inclusive, counting from 0.
     * @param end The selection end, exclusive, counting from 0.
     */
    default void setSelection(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("Selection start cannot be after selection end.");
        }

        getTextComponent().requestFocus();
        getTextComponent().setSelectionStart(start);
        getTextComponent().setSelectionEnd(end);
    }

    default Value getSelectedLine() {
        int lineStart = getLineAtCharPosition(getTextComponent().getSelectionStart());
        int lineEnd = getLineAtCharPosition(getTextComponent().getSelectionEnd() - 1);

        // No selection; selected line is empty
        if (getSelectedText().stringValue().length() == 0) {
            return new Value();
        }

        return new Value(
                "line " + lineStart +
                        ((lineEnd == lineStart) ? "" : (" to " + lineEnd)) +
                        " of " + getSelectedField()
        );
    }

    default Value getSelectedField() {
        int selectionStart = getTextComponent().getSelectionStart();
        int selectionEnd = getTextComponent().getSelectionEnd();

        // No selection; selected field is empty
        if (selectionStart == selectionEnd) {
            return new Value();
        }

        return new Value(getHyperTalkAddress());
    }

    default Range getSelectedRange() {
        return new Range(getTextComponent().getSelectionStart(), getTextComponent().getSelectionEnd());
    }

    default Value getSelectedChunk() {
        int selectionStart = getTextComponent().getSelectionStart();
        int selectionEnd = getTextComponent().getSelectionEnd();

        // No selection; selected chunk is empty
        if (selectionStart == selectionEnd) {
            return new Value();
        }

        // Chunk expression counts from 1
        selectionStart++;

        return new Value("char " +
                selectionStart +
                (selectionEnd == selectionStart ? "" : (" to " + selectionEnd)) +
                " of " +
                getSelectedField()
        );
    }

    default Value getSelectedText() {
        return getTextComponent().getSelectedText() == null ? new Value() : new Value(getTextComponent().getSelectedText());
    }

    default String getSelectableText() {
        Document doc = getTextComponent().getDocument();
        try {
            return doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

    default int getLineAtCharPosition(int position) {
        String text = getSelectableText();
        int c = 0, line = 1;
        while (c <= position && c < text.length()) {
            if (text.charAt(c++) == '\n') {
                line++;
            }
        }

        return line;
    }
}
