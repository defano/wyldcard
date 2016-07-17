/**
 * CardContextMenu.java
 * @author matt.defano@gmail.com
 * 
 * Implements the context-sensitive menu that appears when the right mouse 
 * button is pressed over a card ("New Button", "New Field").  
 */

package hypercard.gui.menu;

import hypercard.parts.ButtonPart;
import hypercard.parts.CardPart;
import hypercard.parts.FieldPart;
import hypercard.parts.PartException;
import hypercard.runtime.RuntimeEnv;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class CardContextMenu extends JPopupMenu implements Serializable {
private static final long serialVersionUID = -726972665320357697L;

	CardPart card;

    JMenuItem jMenuItemNewButton = new JMenuItem("New Button");
    JMenuItem jMenuItemNewField = new JMenuItem("New Field");
    
    public CardContextMenu (CardPart parent) {
        
        card = parent;
        
        this.add(jMenuItemNewButton);
        this.add(jMenuItemNewField);
        
        jMenuItemNewField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFieldActionPerformed(evt);
            }
        });

        jMenuItemNewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });        
    }
    
    public void newFieldActionPerformed(java.awt.event.ActionEvent evt) {    	
    	Point mouseLoc = RuntimeEnv.getRuntimeEnv().getTheMouseLoc();
        Rectangle rect = new Rectangle(mouseLoc.x, mouseLoc.y, FieldPart.DEFAULT_WIDTH, FieldPart.DEFAULT_HEIGHT);
        
        try {
            card.addField(new FieldPart(rect, card));
        } catch (PartException e) {
            throw new RuntimeException("Failed to create field: " + e.getMessage());
        }
    }

    public void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	Point mouseLoc = RuntimeEnv.getRuntimeEnv().getTheMouseLoc();
    	Rectangle rect = new Rectangle(mouseLoc.x, mouseLoc.y, ButtonPart.DEFAULT_WIDTH, ButtonPart.DEFAULT_HEIGHT);
    	
        try {
            card.addButton(new ButtonPart(rect, card));
        } catch (PartException e) {
            throw new RuntimeException("Failed to create button: " + e.getMessage());
        }
    }    
}
