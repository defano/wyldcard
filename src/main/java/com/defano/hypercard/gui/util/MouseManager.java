/*
 * MouseManager
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.gui.util;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.parts.CardPart;
import com.defano.hypercard.runtime.WindowManager;
import com.defano.hypertalk.exception.HtSemanticException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class MouseManager {

    private static boolean mouseIsDown;
    private static Point clickLoc = new Point();

    private static final Set<MousePressedObserver> pressedObserverSet = new HashSet<>();
    private static final Set<MouseReleasedObserver> releasedObserverSet = new HashSet<>();

    public static void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);
    }

    public interface MousePressedObserver {
        void onMousePressed();
    }

    public interface MouseReleasedObserver {
        void onMouseReleased();
    }

    public static Point getClickLoc() {
        return clickLoc;
    }

    public static Point getMouseLoc() {
        CardPart theCard = WindowManager.getStackWindow().getDisplayedCard();
        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mouseLoc, theCard);

        return mouseLoc;
    }

    public static void dragFrom(Point p1, Point p2, boolean withShift, boolean withOption, boolean withCommand) throws HtSemanticException {

        SwingUtilities.convertPointToScreen(p1, HyperCard.getInstance().getCard());
        SwingUtilities.convertPointToScreen(p2, HyperCard.getInstance().getCard());

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

    public static void clickAt(Point p, boolean withShift, boolean withOption, boolean withCommand) throws HtSemanticException {

        SwingUtilities.convertPointToScreen(p, HyperCard.getInstance().getCard());

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

    public static boolean isMouseDown() {
        return mouseIsDown;
    }

    public static void notifyOnMousePressed(MousePressedObserver observer) {
        pressedObserverSet.add(observer);
    }

    public static void notifyOnMouseReleased(MouseReleasedObserver observer) {
        releasedObserverSet.add(observer);
    }

    private static final AWTEventListener listener = event -> {

        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            mouseIsDown = true;
            clickLoc = getMouseLoc();
            fireOnMousePressed();
        }
        if (event.getID() == MouseEvent.MOUSE_RELEASED) {
            mouseIsDown = false;
            fireOnMouseReleased();
        }
    };

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
