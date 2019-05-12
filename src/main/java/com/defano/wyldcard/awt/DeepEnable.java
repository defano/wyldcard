package com.defano.wyldcard.awt;

import java.awt.*;
import java.util.Arrays;

public interface DeepEnable {

    /**
     * Set the enabled of this component and any sub-components to a given state.
     *
     * @param enabled   The desired enabled state
     * @param container The container whose sub-components should be enabled or disabled
     */
    default void setDeepEnabled(boolean enabled, Container container) {
        Arrays.stream(container.getComponents()).forEach(c -> {
            if (c instanceof Container) {
                setDeepEnabled(enabled, (Container) c);
            }
            c.setEnabled(enabled);
        });
    }
}
