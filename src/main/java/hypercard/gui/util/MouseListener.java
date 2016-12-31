package hypercard.gui.util;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class MouseListener {

    private static boolean mouseIsDown;

    private static Set<MousePressedObserver> pressedObserverSet = new HashSet<>();
    private static Set<MouseReleasedObserver> releasedObserverSet = new HashSet<>();

    public interface MousePressedObserver {
        void onMousePressed();
    }

    public interface MouseReleasedObserver {
        void onMouseReleased();
    }

    private static final AWTEventListener listener = event -> {

        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            mouseIsDown = true;
            fireOnMousePressed();
        }
        if (event.getID() == MouseEvent.MOUSE_RELEASED) {
            mouseIsDown = false;
            fireOnMouseReleased();
        }
        if (event.getID() == MouseEvent.MOUSE_ENTERED) {
            // Nothing to do
        }
        if (event.getID() == MouseEvent.MOUSE_EXITED) {
            // Nothing to do
        }
    };

    public static boolean isMouseDown() {
        return mouseIsDown;
    }

    public static void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);
    }

    public static void notifyOnMousePressed(MousePressedObserver observer) {
        pressedObserverSet.add(observer);
    }

    public static void notifyOnMouseReleased(MouseReleasedObserver observer) {
        releasedObserverSet.add(observer);
    }

    private static void fireOnMousePressed() {
        for (MousePressedObserver thisObserver : pressedObserverSet) {
            thisObserver.onMousePressed();
        }

        pressedObserverSet.clear();
    }

    private static void fireOnMouseReleased() {
        for (MouseReleasedObserver thisObserver : releasedObserverSet) {
            thisObserver.onMouseReleased();
        }

        releasedObserverSet.clear();
    }

}
