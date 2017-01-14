package hypercard.paint.canvas;

import java.awt.event.*;

public interface CanvasInteractionListener extends KeyListener {

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on the canvas.
     */
    void mouseClicked(MouseEvent e, int scaleX, int scaleY);

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    void mousePressed(MouseEvent e, int scaleX, int scaleY);

    /**
     * Invoked when a mouse button has been released on a component.
     */
    void mouseReleased(MouseEvent e, int scaleX, int scaleY);

    /**
     * Invoked when the mouse enters a component.
     */
    void mouseEntered(MouseEvent e, int scaleX, int scaleY);

    /**
     * Invoked when the mouse exits a component.
     */
    void mouseExited(MouseEvent e, int scaleX, int scaleY);

    /**
     * Invoked when a mouse button is pressed on a component and then
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
     * delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&amp;Drop implementations,
     * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
     * Drag&amp;Drop operation.
     */
    void mouseDragged(MouseEvent e, int scaleX, int scaleY);

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    void mouseMoved(MouseEvent e, int scaleX, int scaleY);
}
