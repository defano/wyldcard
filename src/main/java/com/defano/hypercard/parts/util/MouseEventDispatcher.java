package com.defano.hypercard.parts.util;

import com.defano.jmonet.canvas.AbstractPaintCanvas;
import com.defano.jmonet.canvas.UndoablePaintCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * A hack to re-dispatch mouse events to parts (Swing components) in the background layer
 * that are obstructed by the foreground (card-layer) graphics canvas.
 *
 * Swing does not support the concept of a component which is "transparent" to mouse events. That
 * is, a component (like a canvas) that covers buttons behind it--even if that component is
 * graphically transparent--prevents mouse events from reaching the obscured components.
 *
 * In order to get the card's foreground canvas to appear on top of parts in the background,
 * and yet still allow the background parts to receive mouse clicks, drags, enters and exits, we
 * have to capture mouse events on the canvas then translate and re-dispatch them on the
 * background parts. What a PITA.
 */
public class MouseEventDispatcher implements MouseListener, MouseMotionListener {

    // Source component whose mouse events we're re-dispatching to components behind it
    private final Component source;

    // Enumerator of parts behind the source
    private final ComponentEnumerator enumerator;

    // Mapping of component hashCode to whether we think this mouse is inside its bounds
    private final Map<Integer,Boolean> mouseWithinMap = new HashMap<>();

    // The component currently being dragged.
    private Component dragging = null;

    public static void bindTo(Component source, ComponentEnumerator delegate) {
        MouseEventDispatcher instance = new MouseEventDispatcher(source, delegate);
        source.addMouseListener(instance);
        source.addMouseMotionListener(instance);
    }

    private MouseEventDispatcher(Component source, ComponentEnumerator enumerator) {
        this.enumerator = enumerator;
        this.source = source;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        delegateEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        delegateEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        delegateEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        delegateEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        delegateEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        delegateEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        delegateEvent(e);
    }

    /**
     * Attempts to find a component behind the source at the event's location and, if found,
     * translates the event and re-dispatches it to the found component.
     *
     * @param e The event to re-dispatch.
     */
    private void delegateEvent(MouseEvent e) {

        // Stop tracking a drag event
        if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            stopDragging();
        }

        // Detect and send mouseEnter and mouseExit events
        synthesizeChildEntryExitEvents(e);

        // Drag in progress; continue to send event only to component that was originally dragged
        if (isDragging() && e.getID() == MouseEvent.MOUSE_DRAGGED) {
            MouseEvent localEvent = SwingUtilities.convertMouseEvent(source, e, dragging);
            dragging.dispatchEvent(localEvent);
        }

        // No drag in progress; dispatch normally
        else {
            Component c = findHit(e);       // Find component behind source
            if (c != null) {

                // TODO: Canvas shouldn't behave unusually in this respect.
                if (c instanceof UndoablePaintCanvas) {
                    ((UndoablePaintCanvas)c).getSurface().dispatchEvent(e);
                }

                // Obscured components will not automatically receive focus; force focus as needed
                if (e.getID() == MouseEvent.MOUSE_CLICKED ||
                        e.getID() == MouseEvent.MOUSE_DRAGGED ||
                        e.getID() == MouseEvent.MOUSE_PRESSED) {
                    c.requestFocus();
                }

                // Translate and dispatch event
                MouseEvent localEvent = SwingUtilities.convertMouseEvent(source, e, c);
                c.dispatchEvent(localEvent);

                // Track that we're dragging a component
                if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                    startDragging(c);
                }
            }
        }
    }

    /**
     * Track and dispatch mouse-enter and mouse-exit events on the background parts.
     *
     * This is a bit more complicated than first expected; we cannot simply translate the
     * source component's mouseEnter/mouseExit events as those fire only when the
     * mouse enters/exits the source, NOT when entering or exiting a component behind it.
     *
     * @param e The source's mouse event
     */
    private void synthesizeChildEntryExitEvents(MouseEvent e) {

        // Only care about mouseMoved events
        if (e.getID() == MouseEvent.MOUSE_MOVED) {

            for (Component c : enumerator.getComponentsInZOrder()) {

                MouseEvent localEvent = SwingUtilities.convertMouseEvent(source, e, c);

                if (didMouseEnter(c, e)) {
                    c.dispatchEvent(synthesizeEvent(c, localEvent, MouseEvent.MOUSE_ENTERED));
                }

                if (didMouseExit(c, e)) {
                    c.dispatchEvent(synthesizeEvent(c, localEvent, MouseEvent.MOUSE_EXITED));
                }
            }
        }
    }

    /**
     * Detect if the mouse has entered the given component.
     *
     * @param c The component which should be checked for mouse entry
     * @param e The mouseMove event to track
     * @return True if the mouse has entered the component, false otherwise.
     */
    private boolean didMouseEnter(Component c, MouseEvent e) {
        Point localPoint = SwingUtilities.convertPoint(source, e.getPoint(), c);
        if (!isMouseWithin(c) && c.contains(localPoint)) {
            mouseWithinMap.put(c.hashCode(), true);
            return true;
        }

        return false;
    }

    /**
     * Detect if the mouse has exited the given component.
     *
     * @param c The component which should be checked for mouse exit
     * @param e The mouseMove event to track
     * @return True if the mouse has exited the component, false otherwise.
     */
    private boolean didMouseExit(Component c, MouseEvent e) {
        Point localPoint = SwingUtilities.convertPoint(source, e.getPoint(), c);
        if (isMouseWithin(c) && !c.contains(localPoint)) {
            mouseWithinMap.put(c.hashCode(), false);
            return true;
        }

        return false;
    }

    /**
     * Determines if the mouse is currently inside the bounds of the given component based
     * on the last mouseMove event that was dispatched.
     *
     * @param c The component to check
     * @return True if the mouse is in the components bounds; false otherwise
     */
    private boolean isMouseWithin(Component c) {
        return mouseWithinMap.containsKey(c.hashCode()) && mouseWithinMap.get(c.hashCode());
    }

    /**
     * Creates a copy of the given MouseEvent but with its event ID and source component
     * replaced by the given arguments.
     *
     * @param source The source of the event to be created
     * @param e The MouseEvent to copy
     * @param newId The new event ID
     * @return The new MouseEvent
     */
    private MouseEvent synthesizeEvent(Component source, MouseEvent e, int newId) {
        return new MouseEvent(source,
                newId,
                e.getWhen(),
                e.getModifiers(),
                e.getX(),
                e.getY(),
                e.getClickCount(),
                e.isPopupTrigger(),
                e.getButton());
    }

    /**
     * Track that the given component is being dragged.
     * @param c The component being dragged.
     */
    private void startDragging(Component c) {
        dragging = c;
    }

    /**
     * Track that the user has stopped dragging.
     */
    private void stopDragging() {
        dragging = null;
    }

    /**
     * Determine if a component is being dragged based on the last event dispatched.
     * @return True if a component is being dragged; false otherwise.
     */
    private boolean isDragging() {
        return dragging != null;
    }

    /**
     * Find the front-most background component which is underneath the point of the given MouseEvent.
     * @param e The mouse event
     * @return The component "hit" by the event or null if no component is underneath the event.
     */
    private Component findHit(MouseEvent e) {
        Component[] components = enumerator.getComponentsInZOrder();

        for (int index = components.length - 1; index >= 0; index--) {

            Component c = components[index];
            Point hitPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), c);

            // Is anything underneath the event?
            if (c.contains(hitPoint)) {

                // Special case: found component is a container... uh oh
                if (c instanceof Container) {

                    // Give priority to scroll pane's viewport if it's hit
                    if (c instanceof JScrollPane) {
                        Component viewPortView = ((JScrollPane)c).getViewport().getView();
                        Point childHidPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), c);
                        if (viewPortView.contains(childHidPoint)) {
                            return viewPortView;
                        }
                    }

                    for (Component child : ((Container) c).getComponents()) {
                        Point childHidPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), child);
                        if (child.contains(childHidPoint)) {
                            return child;
                        }
                    }
                }

                return c;
            }

        }

        return null;
    }
}