package com.defano.hypercard.parts.button;

import com.defano.hypertalk.ast.model.Value;

import javax.swing.*;

public interface IconAlignable {

    /**
     * Gets the JComponent displaying an Icon. Must be of type JLabel or JButton.
     * @return The JComponent displaying an icon.
     */
    JComponent getIconComponent();

    default void setIconAlignment(Value value) {
        switch (value.toString().toLowerCase()) {
            case "bottom":
                setHorizontalTextPosition(SwingConstants.CENTER);
                setVerticalTextPosition(SwingConstants.TOP);
                break;

            case "left":
                setHorizontalTextPosition(SwingConstants.RIGHT);
                setVerticalTextPosition(SwingConstants.CENTER);
                break;

            case "right":
                setHorizontalTextPosition(SwingConstants.LEFT);
                setVerticalTextPosition(SwingConstants.CENTER);
                break;

            case "top":
            default:
                setHorizontalTextPosition(SwingConstants.CENTER);
                setVerticalTextPosition(SwingConstants.BOTTOM);
                break;
        }
    }

    default void setHorizontalTextPosition(int position) {
        JComponent component = getIconComponent();
        if (component instanceof JLabel) {
            ((JLabel) component).setHorizontalTextPosition(position);
        } else if (component instanceof JButton) {
            ((JButton) component).setHorizontalTextPosition(position);
        } else {
            throw new IllegalStateException("Bug! Unimplemented component type: " + component);
        }
    }

    default void setVerticalTextPosition(int position) {
        JComponent component = getIconComponent();
        if (component instanceof JLabel) {
            ((JLabel) component).setVerticalTextPosition(position);
        } else if (component instanceof JButton) {
            ((JButton) component).setVerticalTextPosition(position);
        } else {
            throw new IllegalStateException("Bug! Unimplemented component type: " + component);
        }
    }

}
