package com.defano.hypercard.parts.field;

import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.runtime.context.HyperCardProperties;
import com.defano.hypercard.runtime.context.SelectionContext;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public interface ManagedSelection {

    /**
     * Gets the JTextComponent containing the selectable text.
     * @return The component containing the selectable text.
     */
    JTextComponent getTextComponent();

    /**
     * Gets a HyperTalk expression that refers to this component, like 'card field id 1' or 'the message box'.
     * @return A HyperTalk expression referring to this component
     */
    String getHyperTalkAddress();

    /**
     * Gets a part specifier that refers to this component.
     * @return The part specifier.
     */
    PartSpecifier getPartSpecifier();

    PartModel getPartModel();

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

    /**
     * Gets a HyperTalk expression representing the current line selection (like 'line 1 to 3 of card field id 7', or
     * 'empty' if no selection exists.
     *
     * @return An expression representing the current line selection
     */
    default Value getSelectedLineExpression() {
        int lineStart = getLineAtCharPosition(getTextComponent().getSelectionStart());
        int lineEnd = getLineAtCharPosition(getTextComponent().getSelectionEnd() - 1);

        // No selection; selected line is empty
        if (getSelectedText().stringValue().length() == 0) {
            return new Value();
        }

        return new Value(
                "line " + lineStart +
                        ((lineEnd == lineStart) ? "" : (" to " + lineEnd)) +
                        " of " + getSelectedFieldExpression()
        );
    }

    /**
     * Gets a HyperTalk expression representing the current field selection (like 'field id 3' or 'the message'), or
     * 'empty' if no selection exists.
     *
     * @return An expression representing the current field selection.
     */
    default Value getSelectedFieldExpression() {
        int selectionStart = getTextComponent().getSelectionStart();
        int selectionEnd = getTextComponent().getSelectionEnd();

        // No selection; selected field is empty
        if (selectionStart == selectionEnd) {
            return new Value();
        }

        return new Value(getHyperTalkAddress());
    }

    /**
     * Gets a HyperTalk expression representing the current chunk selection (like 'char 11 to 17 of field id 3'), or
     * 'empty' if no selection exists.
     *
     * @return An expression representing the current chunk selection.
     */
    default Value getSelectedChunkExpression() {
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
                getSelectedFieldExpression()
        );
    }

    /**
     * Gets the range of characters selected on this component.
     * @return The selection range.
     */
    default Range getSelectedRange() {
        return new Range(getTextComponent().getSelectionStart(), getTextComponent().getSelectionEnd());
    }

    /**
     * Gets the text of the current selection or 'empty' if no selection exists.
     * @return The currently selected text.
     */
    default Value getSelectedText() {
        return getTextComponent().getSelectedText() == null ? new Value() : new Value(getTextComponent().getSelectedText());
    }

    /**
     * Gets the entire selectable contents of this component (that is, all of the text, not just the selected text).
     * @return The entire text of this component.
     */
    default String getSelectableText() {
        Document doc = getTextComponent().getDocument();
        try {
            return doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            return "";
        }
    }

    /**
     * Gets the line number at which the given position falls.
     *
     * @param position The position of character whose line should be determined.
     * @return The line (counting from 1) where the character is found.
     */
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

    /**
     * Updates the HyperCard properties and selection context with the active selection.
     */
    default void updateSelectionContext() {
        getPartModel().defineProperty(HyperCardProperties.PROP_SELECTEDTEXT, getSelectedText(), true);
        getPartModel().defineProperty(HyperCardProperties.PROP_SELECTEDCHUNK, getSelectedChunkExpression(), true);
        getPartModel().defineProperty(HyperCardProperties.PROP_SELECTEDLINE, getSelectedLineExpression(), true);

        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_SELECTEDTEXT, getSelectedText(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_SELECTEDCHUNK, getSelectedChunkExpression(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_SELECTEDFIELD, getSelectedFieldExpression(), true);
        HyperCardProperties.getInstance().defineProperty(HyperCardProperties.PROP_SELECTEDLINE, getSelectedLineExpression(), true);

        SelectionContext.getInstance().setTheSelection(getPartSpecifier(), getSelectedRange());
    }
}
