/**
 * PartResizer.java
 * @author matt.defano@motorola.com
 * 
 * Provides the ability for the user to resize a part within the card panel
 * of the main window. (Not nearly as trivial as one might assume.)
 */

package hypercard.gui;

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

public class PartResizer implements MouseListener, Serializable {
private static final long serialVersionUID = -900632519974122141L;

	public final int RESIZER_REFRESH_MS = 10;
	public final int MIN_WIDTH = 20;
	public final int MIN_HEIGHT = 20;
	
	private Part part;
	private Component within;
	private boolean done = false;
	
    private class MoverTask extends TimerTask {
    	public void run () {    	
        	Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        	SwingUtilities.convertPointFromScreen(mouseLoc, within);
        	        	
        	Point partLoc = part.getComponent().getLocation();
        	int newWidth = mouseLoc.x - partLoc.x;
        	int newHeight = mouseLoc.y - partLoc.y;

        	try {
        		if (newWidth >= MIN_WIDTH)
        			part.setProperty("width", new Value(newWidth));
        		
        		if (newHeight >= MIN_HEIGHT)
        			part.setProperty("height", new Value(newHeight));
        	} catch (Exception e) {
        		throw new RuntimeException(e.getMessage());
        	}
        	
       		if (!done)
       			new Timer().schedule(new MoverTask(), RESIZER_REFRESH_MS);       		
    	}
    }
    
    public PartResizer (Part part, Component within) {
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
