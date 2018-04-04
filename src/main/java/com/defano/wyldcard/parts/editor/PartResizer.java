package com.defano.wyldcard.parts.editor;

import com.defano.wyldcard.awt.MouseManager;
import com.defano.wyldcard.parts.Part;
import com.defano.wyldcard.parts.model.PartModel;
import com.defano.wyldcard.awt.KeyboardManager;
import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import javax.swing.*;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Provides the ability for the user to resize a part within the card panel of the stack window.
 */
public class PartResizer {

    private final static int SNAP_TO_GRID_SIZE = 10;
    private final static int RESIZER_REFRESH_MS = 10;
    private final static int MIN_WIDTH = 20;
    private final static int MIN_HEIGHT = 20;

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private final WeakReference<Part> part;
    private final WeakReference<Component> within;
    private boolean done = false;
    private final Rectangle originalBounds;
    
    private class ResizerTask implements Runnable {

        @Override
        public void run () {
            ExecutionContext context = new ExecutionContext();
            Part partInst = part.get();
            Component withinInst = within.get();

            if (partInst != null && withinInst != null) {

                // Don't resize if owning window does not have focus
                if (!SwingUtilities.getWindowAncestor(withinInst).equals(FocusManager.getCurrentManager().getFocusedWindow())) {
                    return;
                }

                Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(mouseLoc, withinInst);
                Point partLoc = originalBounds.getLocation();

                int newWidth = KeyboardManager.getInstance().isShiftDown() ? ((mouseLoc.x / SNAP_TO_GRID_SIZE) * SNAP_TO_GRID_SIZE) - partLoc.x : mouseLoc.x - partLoc.x;
                int newHeight = KeyboardManager.getInstance().isShiftDown() ? ((mouseLoc.y / SNAP_TO_GRID_SIZE) * SNAP_TO_GRID_SIZE) - partLoc.y : mouseLoc.y - partLoc.y;

                try {
                    if (newWidth >= MIN_WIDTH)
                        partInst.setProperty(context, PartModel.PROP_WIDTH, new Value(newWidth));

                    if (newHeight >= MIN_HEIGHT)
                        partInst.setProperty(context, PartModel.PROP_HEIGHT, new Value(newHeight));

                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }

                if (!done) {
                    executor.schedule(this, RESIZER_REFRESH_MS, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public PartResizer (Part part, Component within) {
        this.part = new WeakReference<>(part);
        this.within = new WeakReference<>(within);
        this.originalBounds = new Rectangle(part.getRect(new ExecutionContext()));

        MouseManager.getInstance().notifyOnMouseReleased(() -> done = true);
        executor.schedule(new ResizerTask(), 0, TimeUnit.MILLISECONDS);
    }
}
