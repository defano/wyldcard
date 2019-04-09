package com.defano.wyldcard.parts.field;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.utils.Range;

public interface AddressableSelection {

    /**
     * Gets a HyperTalk expression that refers to this component, like 'card field id 1' or 'the message box'.
     *
     * @param context The execution context.
     * @return A HyperTalk expression referring to this component
     */
    String getHyperTalkAddress(ExecutionContext context);

    /**
     * Gets a part specifier that refers to this component.
     *
     * @param context The execution context.
     * @return The part specifier.
     */
    PartSpecifier getPartSpecifier(ExecutionContext context);

    /**
     * Gets the model associated with the selectable text element.
     *
     * @return The selectable text model.
     */
    SelectableTextModel getSelectableTextModel();

    /**
     * Requests focus and sets a range of selected text in this field. If the start and end positions are equal, no
     * text is selected but the caret is moved to the given position.
     *
     * @param context The execution context.
     * @param start   The selection start, inclusive, counting from 0.
     * @param end     The selection end, exclusive, counting from 0.
     */
    default void setSelection(ExecutionContext context, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("Selection start cannot be after selection end.");
        }

        getSelectableTextModel().setSelection(context, new Range(start, end));
    }

    /**
     * Gets a HyperTalk expression representing the current line selection (like 'line 1 to 3 of card field id 7'), or
     * 'empty' if no selection exists.
     *
     * @param context The execution context.
     * @return An expression representing the current line selection
     */
    default Value getSelectedLineExpression(ExecutionContext context) {
        String selectedText = getSelectedText(context).toString();

        int lineStart = getLineAtCharPosition(context, getSelectableTextModel().getSelection(context).start);

        // When selection ends with '\n', don't consider that newline in line calculation
        int lineEnd = selectedText.endsWith("\n") ?
                getLineAtCharPosition(context, getSelectableTextModel().getSelection(context).end - 2) :
                getLineAtCharPosition(context, getSelectableTextModel().getSelection(context).end - 1);

        // No selection; selected line is empty
        if (getSelectedText(context).toString().length() == 0) {
            return new Value();
        }

        return new Value(
                "line " + lineStart +
                        ((lineEnd == lineStart) ? "" : (" to " + lineEnd)) +
                        " of " + getSelectedFieldExpression(context)
        );
    }

    /**
     * Gets a HyperTalk expression representing the current field selection (like 'field id 3' or 'the message'), or
     * 'empty' if no selection exists.
     *
     * @param context The execution context.
     * @return An expression representing the current field selection.
     */
    default Value getSelectedFieldExpression(ExecutionContext context) {
        int selectionStart = getSelectableTextModel().getSelection(context).start;
        int selectionEnd = getSelectableTextModel().getSelection(context).end;

        // No selection; selected field is empty
        if (selectionStart == selectionEnd) {
            return new Value();
        }

        return new Value(getHyperTalkAddress(context));
    }

    /**
     * Gets a HyperTalk expression representing the current chunk selection (like 'char 11 to 17 of field id 3'), or
     * 'empty' if no selection exists.
     *
     * @param context The execution context.
     * @return An expression representing the current chunk selection.
     */
    default Value getSelectedChunkExpression(ExecutionContext context) {
        int selectionStart = getSelectableTextModel().getSelection(context).start;
        int selectionEnd = getSelectableTextModel().getSelection(context).end;

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
                getSelectedFieldExpression(context)
        );
    }

    /**
     * Gets the text of the current selection or 'empty' if no selection exists.
     *
     * @param context The execution context.
     * @return The currently selected text.
     */
    default Value getSelectedText(ExecutionContext context) {
        Range selection = getSelectableTextModel().getSelection(context);
        String selectedText = getSelectableText(context);

        if (selection.length() == 0) {
            return new Value();
        }

        return new Value(getSelectableText(context).substring(
                selection.start < 0 ? 0 : selection.start,
                selection.end >= selectedText.length() ? selectedText.length() : selection.end));
    }

    /**
     * Gets the entire selectable contents of this component (that is, all of the text, not just the selected text).
     *
     * @param context The execution context.
     * @return The entire text of this component.
     */
    default String getSelectableText(ExecutionContext context) {
        return getSelectableTextModel().getText(context);
    }

    /**
     * Gets the line number at which the given position falls.
     *
     * @param context  The execution context.
     * @param position The position of character whose line should be determined.
     * @return The line (counting from 1) where the character is found.
     */
    default int getLineAtCharPosition(ExecutionContext context, int position) {
        String text = getSelectableText(context);

        int c = 0, line = 1;
        while (c <= position && c < text.length() - 1) {
            if (text.charAt(c++) == '\n') {
                line++;
            }
        }

        return line;
    }

    /**
     * Updates the HyperCard selection context with the active selection.
     *
     * @param context           The execution context.
     * @param selection         The range of characters in the current selection; a zero-length range indicates no selection.
     * @param isSystemSelection True if this selection qualifies as the global, "system" selection. That is, when
     *                          true, this selection is addressable as 'the selection'; when false, the selection
     */
    default void updateSelectionContext(ExecutionContext context, Range selection, boolean isSystemSelection) {
        getSelectableTextModel().onViewDidUpdateSelection(selection);

        if (isSystemSelection) {
            WyldCard.getInstance().getSelectionManager().setSelection(getPartSpecifier(context), getSelectableTextModel().getSelection(context));
        }
    }
}
