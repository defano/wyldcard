/**
 * CardPart.java
 * @author matt.defano@motorola.com
 * 
 * Implements a card part by extending the Swing panel object. 
 */

package hypercard.parts;

import hypercard.context.PartsTable;
import hypercard.gui.CardContextMenu;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.exception.HtSemanticException;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JPanel;

public class CardPart extends JPanel implements MouseListener, Serializable {
private static final long serialVersionUID = 742164164633903146L;

	private PartsTable fields;	
	private PartsTable buttons;
    private int nextId = 0;	
    private File cardFile;

	public CardPart () {
		super();

        this.setComponentPopupMenu(new CardContextMenu(this));
        this.addMouseListener(this);
        this.setLayout(null);
        
		fields = new PartsTable();
		buttons = new PartsTable();
	}
	
    public void partOpened () {
        this.setComponentPopupMenu(new CardContextMenu(this));
        
        fields.sendPartOpened();
        buttons.sendPartOpened();
    }
    
    public static CardPart openCard (File file) {
        try {           
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            CardPart card = (CardPart)ois.readObject();
            ois.close();
            
            card.partOpened();
            card.cardFile = file;
            
            return card;
        } catch (Exception e) {
            throw new RuntimeException ("Failed to load card: " + e.getMessage());
        }                
    }
    
    public void saveCard (File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();          
            
            cardFile = file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save card: " + e.getMessage());
        }
    }
    
    public File getCardFile () {
    	return cardFile;
    }
    
	public void addField (FieldPart field) throws PartException {
		fields.addPart(field);

        this.add(field);
        field.setBounds(field.getRect());
        this.validate();
	}
	
	public void removeField (FieldPart field) {
		fields.removePart(field);
		
		this.remove(field);
		this.validate();
		this.repaint();
	}
	
	public void addButton (ButtonPart button) throws PartException {
		buttons.addPart(button);
		
		this.add(button);
		button.setBounds(button.getRect());
		this.validate();
	}

	public void removeButton (ButtonPart button) {
		fields.removePart(button);
		
		this.remove(button);
		this.validate();
		this.repaint();
	}

	public Part getPart (PartSpecifier ps) throws PartException {
		if (ps.type() == PartType.FIELD)
			return fields.getPart(ps);		
		else if (ps.type() == PartType.BUTTON)
			return buttons.getPart(ps);
		else
			throw new RuntimeException("Unhandled part type");
	}
    
    public int getNextPartId () {
        return nextId++;
    }

	public Value executeUserFunction (PartSpecifier ps, String function, ArgumentList arguments) 
	throws HtSemanticException, PartException
	{
		return getPart(ps).executeUserFunction(function, arguments);
	}
		
    public void mousePressed(MouseEvent e) {
    	RuntimeEnv.getRuntimeEnv().setTheMouse(true);
    }

    public void mouseReleased(MouseEvent e) {
    	RuntimeEnv.getRuntimeEnv().setTheMouse(false);    	
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
}
