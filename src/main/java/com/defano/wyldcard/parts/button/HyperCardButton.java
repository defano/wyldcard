package com.defano.wyldcard.parts.button;

import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;
import com.defano.wyldcard.parts.util.LifecycleObserver;

/**
 * An interface for classes that extend a Swing component (like JButton) and represent the Swing component or component
 * hierarchy associated with a HyperCard button part.
 *
 * This interface must be applied to a class that extends JComponent.
 */
public interface HyperCardButton extends PropertyChangeObserver, LifecycleObserver {

    /**
     * Gets the {@link ToolEditablePart} object associated with this button.
     * @return The ToolEditablePart object of this button.
     */
    ToolEditablePart getToolEditablePart();
}
