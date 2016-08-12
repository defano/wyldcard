/**
 * PartMover.java
 * @author matt.defano@motorola.com
 * 
 * Provides the ability for the user to move a part around the card panel
 * of the main window. (Not nearly as trivial as one might assume.)
 */

package hypercard.parts;

import hypercard.gui.util.ModifierKeyListener;
import hypertalk.ast.common.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PartMover implements MouseListener {

    public final int MOVER_REFRESH_MS = 10;
    public final int SNAP_TO_GRID_SIZE = 10;

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Part part;
    private Component within;
    private boolean done = false;
    
    private class MoverTask implements Runnable {
        public void run () {        
            Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(mouseLoc, within);

            int horizCenter = part.getComponent().getWidth() / 2;
            int vertCenter = part.getComponent().getHeight() / 2;

            int newTop = ModifierKeyListener.isShiftDown ? (((mouseLoc.y - vertCenter) / SNAP_TO_GRID_SIZE) * SNAP_TO_GRID_SIZE) : mouseLoc.y - vertCenter;
            int newLeft = ModifierKeyListener.isShiftDown ? (((mouseLoc.x - horizCenter) / SNAP_TO_GRID_SIZE) * SNAP_TO_GRID_SIZE) : mouseLoc.x - horizCenter;

            try {
                part.setProperty("top", new Value(newTop));
                part.setProperty("left", new Value(newLeft));
            } catch (Exception e) {
                throw new RuntimeException (e);
            }
               
               if (!done) {
                executor.schedule(this, MOVER_REFRESH_MS, TimeUnit.MILLISECONDS);
            }
        }
    }
    
    public PartMover (Part part, Component within) {
        this.part = part;
        this.within = within;

        part.getComponent().addMouseListener(this);
        within.addMouseListener(this);

        executor.schedule(new MoverTask(), 0, TimeUnit.MILLISECONDS);
    }

    public void mousePressed(MouseEvent e) {
        done = true;
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
}
