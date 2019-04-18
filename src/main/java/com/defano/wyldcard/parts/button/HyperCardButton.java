package com.defano.wyldcard.parts.button;

import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.model.PropertyChangeObserver;

/**
 * A "marker" interface for classes that extend a Swing component (like JButton) and represent the Swing component
 * associated with a HyperCard button part.
 */
public interface HyperCardButton extends PropertyChangeObserver {

    ToolEditablePart getToolEditablePart();
}
