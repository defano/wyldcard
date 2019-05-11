package com.defano.wyldcard.runtime.manager;

import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifier.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.hypertalk.util.Range;
import com.defano.wyldcard.part.field.AddressableSelection;
import com.defano.wyldcard.part.model.PartModel;
import com.defano.wyldcard.runtime.ExecutionContext;

/**
 * Manages WyldCard's view of 'the selection'.
 */
public interface SelectionManager {

    /**
     * Specifies a range of characters in a given part that represents 'the selection'.
     *
     * @param selectionPart  The part holding the current selection.
     * @param selectionRange The range of characters in this part that is selected.
     */
    void setSelection(PartSpecifier selectionPart, Range selectionRange);

    /**
     * Gets the range of characters (in {@link #getSelectionOwningPart(ExecutionContext)} that is currently selected.
     *
     * @return The range of selected characters.
     */
    Range getSelectionRange();

    /**
     * Gets the part currently holding the active selection.
     *
     * @param context The execution context.
     * @return The model associated with the part holding the active selection.
     * @throws HtSemanticException Thrown if there is no selection.
     */
    PartModel getSelectionOwningPart(ExecutionContext context) throws HtException;

    /**
     * Gets the AddressableSelection object associated with the active selection.
     *
     * @param context The execution context.
     * @return The AddressableSelection
     * @throws HtSemanticException Thrown if there is no selection.
     */
    AddressableSelection getManagedSelection(ExecutionContext context) throws HtException;

    /**
     * Gets the currently selected text.
     *
     * @param context The execution context.
     * @return The current selection.
     * @throws HtSemanticException Thrown if there is no selection.
     */
    Value getSelection(ExecutionContext context) throws HtException;

    /**
     * Get the location (point) in card-relative coordinates of the top-left corner of the selection.
     *
     * @return The location, in card-relative coordinates, of the selection
     */
    Value getSelectedLoc();

    /**
     * Sets the location (point) in card-relative coordinates of the top-left corner of the selection.
     *
     * @param selectedLoc The location, in card-relative coordinates, of the selection
     */
    void setSelectedLoc(Value selectedLoc);

    /**
     * Returns the value represented by 'the clickText' function (the last word clicked in a field), or empty if no
     * text has been clicked.
     *
     * @return The click text.
     */
    Value getClickText();

    /**
     * Sets the value returned by 'the clickText' function
     *
     * @param clickText The text that was last clicked
     */
    void setClickText(Value clickText);

    /**
     * Gets a HyperTalk expression representing the chunk of text last clicked in a field, for example 'chars 3 to 7 of
     * card field 3'
     *
     * @return The clickChunk value, or empty, if no text has been clicked.
     */
    Value getClickChunk();

    /**
     * Sets the value represented by 'the clickText' function (the last word clicked in a field).
     *
     * @param clickChunk The clickText value.
     */
    void setClickChunk(Value clickChunk);

    /**
     * Gets the number of the line (counting from 1) last clicked in a text field.
     *
     * @return The clickLine value.
     */
    Value getClickLine();

    /**
     * Sets the number of the line (counting from 1) last clicked in a text field.
     *
     * @param clickLine The clickLine value.
     */
    void setClickLine(Value clickLine);
}
