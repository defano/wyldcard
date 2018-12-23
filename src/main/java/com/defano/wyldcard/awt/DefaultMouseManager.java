package com.defano.wyldcard.awt;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides an AWT-level view of mouse actions, plus routines for scripted control of the mouse.
 */
@Singleton
public class DefaultMouseManager implements MouseManager {

    private static boolean mouseIsDown;
    private static Point clickLoc = new Point();
    private static Long clickTime;

    private static final Set<MousePressedObserver> pressedObserverSet = new HashSet<>();
    private static final Set<MouseReleasedObserver> releasedObserverSet = new HashSet<>();

    public interface MousePressedObserver {
        void onMousePressed();
    }

    public interface MouseReleasedObserver {
        void onMouseReleased();
    }

    public void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);
    }

    public Point getClickLoc() {
        return clickLoc;
    }

    public Long getClickTimeMs() {
        return clickTime;
    }

    public Point getMouseLoc(ExecutionContext context) {
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();

        if (WyldCard.getInstance().getWindowManager().getFocusedStackWindow() != null) {
            CardPart theCard = context.getCurrentCard();
            SwingUtilities.convertPointFromScreen(mouseLoc, theCard);
        }

        return mouseLoc;
    }

    public void dragFrom(Point p1, Point p2, boolean withShift, boolean withOption, boolean withCommand) throws HtSemanticException {

        Component cardComponent = WyldCard.getInstance().getFocusedCard();
        SwingUtilities.convertPointToScreen(p1, cardComponent);
        SwingUtilities.convertPointToScreen(p2, cardComponent);

        try {
            Robot r = new Robot();
            r.setAutoWaitForIdle(true);
            r.setAutoDelay(20);

            r.mouseMove(p1.x, p1.y);

            if (withShift) r.keyPress(KeyEvent.VK_SHIFT);
            if (withOption) r.keyPress(KeyEvent.VK_META);
            if (withCommand) r.keyPress(KeyEvent.VK_CONTROL);
            r.mousePress(InputEvent.BUTTON1_MASK);

            r.mouseMove(p2.x, p2.y);

            r.mouseRelease(InputEvent.BUTTON1_MASK);
            if (withShift) r.keyRelease(KeyEvent.VK_SHIFT);
            if (withOption) r.keyRelease(KeyEvent.VK_META);
            if (withCommand) r.keyRelease(KeyEvent.VK_CONTROL);

        } catch (AWTException e) {
            throw new HtSemanticException("Sorry, scripted dragging is not supported on this system.");
        }
    }

    public void clickAt(Point p, boolean withShift, boolean withOption, boolean withCommand) throws HtSemanticException {

        SwingUtilities.convertPointToScreen(p, WyldCard.getInstance().getFocusedCard());

        try {
            Robot r = new Robot();
            r.setAutoWaitForIdle(true);
            r.setAutoDelay(20);

            r.mouseMove(p.x, p.y);

            if (withShift) r.keyPress(KeyEvent.VK_SHIFT);
            if (withOption) r.keyPress(KeyEvent.VK_META);
            if (withCommand) r.keyPress(KeyEvent.VK_CONTROL);
            r.mousePress(InputEvent.BUTTON1_MASK);

            r.mouseRelease(InputEvent.BUTTON1_MASK);
            if (withShift) r.keyRelease(KeyEvent.VK_SHIFT);
            if (withOption) r.keyRelease(KeyEvent.VK_META);
            if (withCommand) r.keyRelease(KeyEvent.VK_CONTROL);

        } catch (AWTException e) {
            throw new HtSemanticException("Sorry, scripted clicking is not supported on this system.");
        }
    }

    public boolean isMouseDown() {
        return mouseIsDown;
    }

    public void notifyOnMousePressed(MousePressedObserver observer) {
        pressedObserverSet.add(observer);
    }

    public void notifyOnMouseReleased(MouseReleasedObserver observer) {
        releasedObserverSet.add(observer);
    }

    private final AWTEventListener listener = event -> {

        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            mouseIsDown = true;
            clickLoc = getMouseLoc(new ExecutionContext());
            clickTime = System.currentTimeMillis();
            fireOnMousePressed();
        }
        if (event.getID() == MouseEvent.MOUSE_RELEASED) {
            mouseIsDown = false;
            fireOnMouseReleased();
        }
    };

    private void fireOnMousePressed() {
        for (MousePressedObserver thisObserver : pressedObserverSet) {
            thisObserver.onMousePressed();
        }

        pressedObserverSet.clear();
    }

    private void fireOnMouseReleased() {
        for (MouseReleasedObserver thisObserver : releasedObserverSet) {
            thisObserver.onMouseReleased();
        }

        releasedObserverSet.clear();
    }

}
