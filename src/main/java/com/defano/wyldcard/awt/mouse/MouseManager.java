package com.defano.wyldcard.awt.mouse;

import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.awt.*;

public interface MouseManager {

    void start();

    Point getClickLoc();

    Long getClickTimeMs();

    Point getMouseLoc(ExecutionContext context);

    void dragFrom(Point p1, Point p2, boolean withShift, boolean withOption, boolean withCommand) throws HtSemanticException;

    void clickAt(Point p, boolean withShift, boolean withOption, boolean withCommand) throws HtSemanticException;

    boolean isMouseDown();

    void notifyOnMousePressed(MousePressedObserver observer);

    void notifyOnMouseReleased(MouseReleasedObserver observer);
}
