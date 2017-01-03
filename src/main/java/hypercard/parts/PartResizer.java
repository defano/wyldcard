/**
 * PartResizer.java
 * @author matt.defano@motorola.com
 * 
 * Provides the ability for the user to resize a part within the card panel
 * of the main window. (Not nearly as trivial as one might assume.)
 */

package hypercard.parts;

import hypercard.gui.util.ModifierKeyListener;
import hypercard.gui.util.MouseManager;
import hypercard.parts.model.AbstractPartModel;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PartResizer {

    public final int SNAP_TO_GRID_SIZE = 10;
    public final int RESIZER_REFRESH_MS = 10;
    public final int MIN_WIDTH = 20;
    public final int MIN_HEIGHT = 20;

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Part part;
    private Component within;
    private boolean done = false;
    
    private class ResizerTask implements Runnable {
        public void run () {        
            Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouseLoc, within);
                        
            Point partLoc = part.getComponent().getLocation();
            int newWidth = ModifierKeyListener.isShiftDown ? ((mouseLoc.x / SNAP_TO_GRID_SIZE) * SNAP_TO_GRID_SIZE) - partLoc.x : mouseLoc.x - partLoc.x;
            int newHeight = ModifierKeyListener.isShiftDown ? ((mouseLoc.y / SNAP_TO_GRID_SIZE) * SNAP_TO_GRID_SIZE) - partLoc.y : mouseLoc.y - partLoc.y;

            try {
                if (newWidth >= MIN_WIDTH)
                    part.setProperty(AbstractPartModel.PROP_WIDTH, new Value(newWidth));
                
                if (newHeight >= MIN_HEIGHT)
                    part.setProperty(AbstractPartModel.PROP_HEIGHT, new Value(newHeight));

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            
            if (!done) {
                executor.schedule(this, RESIZER_REFRESH_MS, TimeUnit.MILLISECONDS);
            }
        }
    }
    
    public PartResizer (Part part, Component within) {
        this.part = part;
        this.within = within;

        MouseManager.notifyOnMousePressed(() -> done = true);

        executor.schedule(new ResizerTask(), 0, TimeUnit.MILLISECONDS);
    }
}
