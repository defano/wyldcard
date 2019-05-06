package com.defano.wyldcard.runtime.manager;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.utils.Range;
import com.defano.wyldcard.parts.field.AddressableSelection;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

public interface SelectionManager {
    /**
     * Specifies a range of characters in a given part that represents 'the selection'.
     * @param selectionPart The part holding the current selection.
     * @param selectionRange The range of characters in this part that is selected.
     */
    void setSelection(PartSpecifier selectionPart, Range selectionRange);

    /**
     * Gets the range of characters (in {@link #getSelectedPart(ExecutionContext)} that is currently selected.
     * @return The range of selected characters.
     */
    Range getSelectionRange();

    /**
     * Gets the part currently holding the active selection.
     * @return The model associated with the part holding the active selection.
     * @throws HtSemanticException Thrown if there is no selection.
     * @param context The execution context.
     */
    PartModel getSelectedPart(ExecutionContext context) throws HtException;

    /**
     * Gets the AddressableSelection object associated with the active selection.
     * @return The AddressableSelection
     * @throws HtSemanticException Thrown if there is no selection.
     * @param context The execution context.
     */
    AddressableSelection getManagedSelection(ExecutionContext context) throws HtException;

    /**
     * Gets the currently selected text.
     * @return The current selection.
     * @throws HtSemanticException Thrown if there is no selection.
     * @param context The execution context.
     */
    Value getSelection(ExecutionContext context) throws HtException;

    Value getSelectedLoc();

    void setSelectedLoc(Value selectedLoc);

    /**
     * Sets the value returned by 'the clickText' function
     * @param clickText The text that was last clicked
     */
    void setClickText(Value clickText);

    /**
     * Returns the value represented by 'the clickText' function (the last word clicked in a field), or empty if no
     * text has been clicked.
     *
     * @return The click text.
     */
    Value getClickText();

    /**
     * Sets the value represented by 'the clickText' function (the last word click in a field).
     * @param clickChunk The clickText value.
     */
    void setClickChunk(Value clickChunk);

    /**
     * Gets a HyperTalk expression representing the chunk of text last clicked in a field, for example 'chars 3 to 7 of
     * card field 3'
     *
     * @return The clickChunk value, or empty, if no text has been clicked.
     */
    Value getClickChunk();

    /**
     * Sets the number of the line (counting from 1) last clicked in a text field.
     * @param clickLine The clickLine value.
     */
    void setClickLine(Value clickLine);

    /**
     * Gets the number of the line (counting from 1) last clicked in a text field.
     * @return The clickLine value.
     */
    Value getClickLine();
}
