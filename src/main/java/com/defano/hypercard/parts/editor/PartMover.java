package com.defano.hypercard.parts.editor;

import com.defano.hypercard.awt.MouseManager;
import com.defano.hypercard.parts.Part;
import com.defano.hypercard.parts.card.CardLayerPart;
import com.defano.hypercard.parts.model.PartModel;
import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A utility allowing users to drag parts around the card. Provides threading the mouse handling.
 */
public class PartMover {

    private static final int MOVER_REFRESH_MS = 10;
    private static final int SNAP_TO_GRID_SIZE = 10;

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final WeakReference<CardLayerPart> part;
    private final WeakReference<Component> within;
    private boolean done = true;
    private Point mouseLocInPart;

    private class MoverTask implements Runnable {
        public void run () {
            Part partInst = part.get();
            Component withinInst = within.get();

            if (partInst != null && withinInst != null) {

                Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mouseLoc, withinInst);

                int newTop = KeyboardManager.isShiftDown ? (((mouseLoc.y - mouseLocInPart.y) / SNAP_TO_GRID_SIZE) * SNAP_TO_GRID_SIZE) : mouseLoc.y - mouseLocInPart.y;
                int newLeft = KeyboardManager.isShiftDown ? (((mouseLoc.x - mouseLocInPart.x) / SNAP_TO_GRID_SIZE) * SNAP_TO_GRID_SIZE) : mouseLoc.x - mouseLocInPart.x;

                try {
                    partInst.setProperty(PartModel.PROP_TOP, new Value(newTop));
                    partInst.setProperty(PartModel.PROP_LEFT, new Value(newLeft));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (!done) {
                    executor.schedule(this, MOVER_REFRESH_MS, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    /**
     * Create a PartMover for moving a given part within a given component.
     * @param part The part to move
     * @param within The parent component in which it should be moved.
     */
    public PartMover(CardLayerPart part, Component within) {
        this.part = new WeakReference<>(part);
        this.within = new WeakReference<>(within);
    }

    /**
     * Begin moving the part; should be invoked when the user has clicked the mouse over the part.
     */
    public void startMoving() {
        CardLayerPart partInst = part.get();

        if (done && partInst != null) {
            done = false;

            Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouseLoc, partInst.getComponent());
            this.mouseLocInPart = new Point(mouseLoc.x, mouseLoc.y);

            MouseManager.notifyOnMouseReleased(() -> done = true);
            executor.schedule(new MoverTask(), 0, TimeUnit.MILLISECONDS);
        }
    }
}
