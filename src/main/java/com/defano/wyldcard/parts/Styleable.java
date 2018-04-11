package com.defano.wyldcard.parts;

import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.*;

/**
 * Represents a part that can be styled (that is, the underlying Swing component can be swapped out at runtime).
 *
 * @param <Style> The style associated with the part.
 * @param <StyledComponent> The (Swing) component associated with the part.
 */
public interface Styleable<Style,StyledComponent> {

    /**
     * Indicates that the Swing component associated with this part has changed and that the part's
     * parent (i.e., the card or background) should update itself accordingly.
     *
     * Because different styles are generally represented by different Swing components, this is the primary means by
     * which HyperCard can swap one button or field style for another.
     *
     * @param context The execution context.
     * @param oldComponent The former component associated with this part
     * @param newComponent The new component
     */
    void replaceViewComponent(ExecutionContext context, Component oldComponent, Component newComponent);

    /**
     * Specifies the style of this part.
     * @param context The execution context.
     * @param style The desired style.
     */
    void setStyle(ExecutionContext context, Style style);

    /**
     * Gets the component associated with the specified style.
     * @param style The style whose component should be returned.
     * @return The component (typically a subclass of JComponent) associated with the requested style.
     */
    StyledComponent getComponentForStyle(Style style);
}
