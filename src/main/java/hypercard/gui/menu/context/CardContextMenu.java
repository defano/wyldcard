/**
 * CardContextMenu.java
 * @author matt.defano@gmail.com
 * 
 * Implements the context-sensitive menu that appears when the right mouse 
 * button is pressed over a card ("New Button", "New Field").  
 */

package hypercard.gui.menu.context;

import hypercard.parts.ButtonPart;
import hypercard.parts.CardPart;
import hypercard.parts.FieldPart;
import hypercard.parts.PartException;
import hypercard.runtime.RuntimeEnv;

import javax.swing.*;
import java.awt.*;

public class CardContextMenu extends JPopupMenu {

	CardPart card;

    JMenuItem jMenuItemNewButton = new JMenuItem("New Button");
    JMenuItem jMenuItemNewField = new JMenuItem("New Field");
    
    public CardContextMenu (CardPart parent) {
        
        card = parent;
        
        this.add(jMenuItemNewButton);
        this.add(jMenuItemNewField);
        
        jMenuItemNewField.addActionListener(evt -> newFieldActionPerformed());
        jMenuItemNewButton.addActionListener(evt -> newButtonActionPerformed());
    }
    
    public void newFieldActionPerformed() {
    	Point mouseLoc = RuntimeEnv.getRuntimeEnv().getTheMouseLoc();
        Rectangle rect = new Rectangle(mouseLoc.x, mouseLoc.y, FieldPart.DEFAULT_WIDTH, FieldPart.DEFAULT_HEIGHT);
        
        try {
            card.addField(FieldPart.fromGeometry(card, rect));
        } catch (PartException e) {
            throw new RuntimeException("Failed to create field: " + e.getMessage());
        }
    }

    public void newButtonActionPerformed() {
    	Point mouseLoc = RuntimeEnv.getRuntimeEnv().getTheMouseLoc();
    	Rectangle rect = new Rectangle(mouseLoc.x, mouseLoc.y, ButtonPart.DEFAULT_WIDTH, ButtonPart.DEFAULT_HEIGHT);
    	
        try {
            card.addButton(ButtonPart.fromGeometry(card, rect));
        } catch (PartException e) {
            throw new RuntimeException("Failed to create button: " + e.getMessage());
        }
    }    
}
