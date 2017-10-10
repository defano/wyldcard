package com.defano.hypercard.parts.field;

import com.defano.hypercard.parts.model.PropertyChangeObserver;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;

public interface FieldComponent extends PropertyChangeObserver {

    /**
     * Gets the editable string currently in this component.
     *
     * @return The text of this component.
     */
    String getText();

    /**
     * Gets the JTextPane portion of this component. Field components are generally comprised of a JScrollPane and a
     * JTextPage; this gets the later component.
     *
     * @return The JTextPane
     */
    JTextPane getTextPane();

    /**
     * Sets whether the text in this component should be user editable.
     *
     * @param editable Make text editable when true
     */
    void setEditable(boolean editable);

    /**
     * Sets the font family of the indicated range of characters in this field; has no effect if the font family
     * is not available on this system.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontFamily    The new font family to apply.
     */
    void setTextFontFamily(int startPosition, int length, Value fontFamily);

    /**
     * Sets the font size (in points) of the indicated range of characters in this field.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontSize      The new font size to apply.
     */
    void setTextFontSize(int startPosition, int length, Value fontSize);

    /**
     * Sets the font style of the indicated range of characters in this field; style should be 'italic', 'bold',
     * 'bold,italic' or 'plain'.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @param fontStyle     The new font style to apply.
     */
    void setTextFontStyle(int startPosition, int length, Value fontStyle);

    /**
     * Gets the font family of the indicated range of characters in the field, or 'mixed' if multiple fonts are present
     * in the range.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return              The name of the font family present in the range of characters or 'mixed' if there are
     * multiple fonts
     */
    Value getTextFontFamily(int startPosition, int length);

    /**
     * Gets the font size of the indicated range of characters in the field, or 'mixed' if multiple sizes are present
     * in the range.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return              The size of the font present in the range of characters or 'mixed' if there are multiple
     * sizes.
     */
    Value getTextFontSize(int startPosition, int length);

    /**
     * Gets the font style of the indicated range of characters in the field, or 'mixed' if multiple styles are present
     * in the range.
     *
     * @param startPosition The index of the first character whose style should change
     * @param length        The number of characters after the start position to apply the style to.
     * @return              The font style present in the range of characters or 'mixed' if there are multiple styles
     */
    Value getTextFontStyle(int startPosition, int length);

    /**
     * Invoked to indicate the part has opened on the card.
     */
    void partOpened();

    /**
     * Invoked to indicate the part has closed on the card.
     */
    void partClosed();
}
