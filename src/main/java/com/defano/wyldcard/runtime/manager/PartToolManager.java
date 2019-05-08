package com.defano.wyldcard.runtime.manager;

import com.defano.wyldcard.parts.ToolEditablePart;
import io.reactivex.Observable;

import java.util.Optional;

/**
 * Manages the selection and z-order of button and field parts.
 */
public interface PartToolManager {
    /**
     * Start the manager.
     * <p>
     * Causes the manager to register itself as a listener of required events; correct behavior of the manager is not
     * assured until it has been started.
     */
    void start();

    /**
     * Deselects any selected part, leaving no active part selection.
     */
    void deselectAllParts();

    /**
     * Adjusts the z-order of the active part selection, bringing the part closer to the front of the view.
     */
    void bringSelectedPartCloser();

    /**
     * Adjusts the z-order of the active part selection, sending the part further from the front of the view.
     */
    void sendSelectedPartFurther();

    /**
     * Deletes the selected part; has no effect if there is no active part selection.
     */
    void deleteSelectedPart();

    /**
     * Gets a ReactiveX observable identifying the active part selection.
     *
     * @return The active part selection observable.
     */
    Observable<Optional<ToolEditablePart>> getSelectedPartProvider();

    /**
     * Gets the part that is currently selected, or null, if there is no selection.
     *
     * @return The current part selection, or null, if there is no selection.
     */
    ToolEditablePart getSelectedPart();

    /**
     * Select the given part, changing the active tool to the field or button tool as necessary. The selected part will
     * be drawn outlined with marching ants to indicate selection.
     *
     * @param part The part to select.
     */
    void setSelectedPart(ToolEditablePart part);
}
