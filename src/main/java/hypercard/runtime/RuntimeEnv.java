/**
 * RuntimeEnv.java
 * @author matt.defano@gmail.com
 * 
 * The HyperCard runtime environment; this is the program's main class and is
 * responsible for initializing the HyperCard window, tracking mouse changes
 * and reporting exceptions to the user.
 */

package hypercard.runtime;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import hypercard.context.GlobalContext;
import hypercard.gui.HcWindow;
import hypercard.parts.CardPart;
import hypercard.parts.PartException;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.HtSyntaxException;

import java.awt.MouseInfo;
import java.awt.Point;
import java.io.Serializable;
import javax.swing.JOptionPane;

public class RuntimeEnv implements Serializable {
private static final long serialVersionUID = 3092430577877088297L;

	private static RuntimeEnv _instance;	
	private HcWindow mainWind;
	private boolean supressMessages = false;
	private boolean mouseIsDown;
	
	public static void main (String argv[]) {
		RuntimeEnv.getRuntimeEnv();
	}
	
	private RuntimeEnv () {
        /* Use this operating systems look and feel for our user interface. If
         * this causes an exception, just ignore it (it's not the end of the
         * world if we can't use the native look anyway).
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("Unable to set the UI look and feel");
        }
        
        // Create the main window, center it on the screen and display it
        mainWind = new HcWindow(new CardPart());
        mainWind.setLocationRelativeTo(null);
        mainWind.setVisible(true);				
	}
	
	public static RuntimeEnv getRuntimeEnv () {
		
		if (_instance == null)
			_instance = new RuntimeEnv();
		
		return _instance;
	}

	public HcWindow getMainWind () {
		return mainWind;
	}

	
	public Point getTheMouseLoc () {
		CardPart theCard = mainWind.getCurrentCard();
		Point mouseLoc = MouseInfo.getPointerInfo().getLocation();	
		SwingUtilities.convertPointFromScreen(mouseLoc, theCard);

		return mouseLoc;
	}
	
	public Value getTheMouseLocValue () {
		Point mouseLoc = getTheMouseLoc();	
		return new Value(String.valueOf(mouseLoc.x) + "," + String.valueOf(mouseLoc.y));
	}
	
	public void setTheMouse (boolean isDown) {
		this.mouseIsDown = isDown;
	}

	public Value getTheMouse () {
		return mouseIsDown ? new Value("down") : new Value("up");
	}
	
	public void setMsgBoxText (Object theMsg) {
		mainWind.setMsgBoxText(theMsg.toString());
	}
	
	public String getMsgBoxText () {
		return mainWind.getMsgBoxText();
	}
	
	public void sendMessage (PartSpecifier ps, String message) throws PartException, HtSyntaxException {
		if (!supressMessages)
			GlobalContext.getContext().get(ps).sendMessage(message);
	}
        
    public void dialogSyntaxError (Exception e) {
        JOptionPane.showMessageDialog(mainWind, "Syntax error: " + e.getMessage());
    }        
}
