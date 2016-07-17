/**
 * PartMover.java
 * @author matt.defano@motorola.com
 * 
 * Provides the ability for the user to move a part around the card panel
 * of the main window. (Not nearly as trivial as one might assume.)
 */

package hypercard.parts;

import hypercard.parts.Part;
import hypertalk.ast.common.Value;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

public class PartMover implements MouseListener, Serializable {
private static final long serialVersionUID = -673067361516960759L;

	public final int MOVER_REFRESH_MS = 10;
	
	private Part part;
	private Component within;
	private boolean done = false;
	
    private class MoverTask extends TimerTask {
    	public void run () {    	
        	Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        	SwingUtilities.convertPointFromScreen(mouseLoc, within);
       		
        	try {
        		part.setProperty("top", new Value(mouseLoc.y));
        		part.setProperty("left", new Value(mouseLoc.x));
        	} catch (Exception e) {
        		throw new RuntimeException (e.getMessage());
        	}
       		
       		if (!done)
       			new Timer().schedule(new MoverTask(), MOVER_REFRESH_MS);       		
    	}
    }
    
    public PartMover (Part part, Component within) {
    	this.part = part;
    	this.within = within;

    	part.getComponent().addMouseListener(this);
    	within.addMouseListener(this);
    	
    	new Timer().schedule(new MoverTask(), 0);
    }

	public void mousePressed(MouseEvent e) {
		done = true;
    }

    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
}
