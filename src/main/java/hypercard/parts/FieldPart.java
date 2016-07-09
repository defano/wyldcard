/**
 * FieldPart.java
 * @author matt.defano@motorola.com
 * 
 * Implements a HyperCard field part user interface by extending the Swing
 * scroll panel. 
 */

package hypercard.parts;

import hypercard.context.GlobalContext;
import hypercard.gui.FieldEditor;
import hypercard.gui.FieldContextMenu;
import hypercard.gui.PartMover;
import hypercard.gui.PartResizer;
import hypercard.gui.ScriptEditor;
import hypercard.runtime.Interpreter;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Script;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;
import hypertalk.properties.Properties;
import hypertalk.properties.PropertyChangeListener;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class FieldPart extends JScrollPane implements Part, MouseListener, PropertyChangeListener, KeyListener, Serializable {
private static final long serialVersionUID = 6441731559079090863L;

	public static final int DEFAULT_WIDTH = 250;
    public static final int DEFAULT_HEIGHT = 100;
    
    public static final String PROP_SCRIPT      = "script";
    public static final String PROP_TEXT        = "text";
    public static final String PROP_ID          = "id";
    public static final String PROP_NAME        = "name";
    public static final String PROP_LEFT        = "left";
    public static final String PROP_TOP         = "top";
    public static final String PROP_WIDTH       = "width";
    public static final String PROP_HEIGHT      = "height";
    public static final String PROP_WRAPTEXT    = "wraptext";
    public static final String PROP_VISIBLE     = "visible";
    public static final String PROP_LOCKTEXT    = "locktext";
    
    private JTextArea text;    
	private Script script;
	private Properties properties;
    private CardPart parent;
	
    public FieldPart (Rectangle geometry, CardPart parent) {
        super();
        
        this.parent = parent;
		this.script = new Script();
        
		initComponents();
		initProperties(geometry);		
    }
    
    public void partOpened() {
        this.setComponentPopupMenu(new FieldContextMenu(this));
    }
    
    private void initProperties (Rectangle geometry) {
        String id = String.valueOf(parent.getNextPartId());
        
		properties = new Properties(this);
		properties.addPropertyChangeListener(this);
		
		properties.defineProperty(PROP_SCRIPT, new Value(), false);
		properties.defineProperty(PROP_TEXT, new Value(), false);
		properties.defineProperty(PROP_ID, new Value(id), true);
		properties.defineProperty(PROP_NAME, new Value("Text Field " + id), false);	
		properties.defineProperty(PROP_LEFT, new Value(geometry.x), false);
		properties.defineProperty(PROP_TOP, new Value(geometry.y), false);
		properties.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
		properties.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
		properties.defineProperty(PROP_WRAPTEXT, new Value(true), false);
		properties.defineProperty(PROP_VISIBLE, new Value(true), false);
		properties.defineProperty(PROP_LOCKTEXT, new Value(false), false);
    }
    
    private void initComponents () {
        text = new JTextArea();
        text.setTabSize(4);
        text.setComponentPopupMenu(new FieldContextMenu(this));
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.addMouseListener(this);    
        text.addKeyListener(this);     
        this.setViewportView(text);        
    }
        
    public void editProperties () {                
    	FieldEditor fe = new FieldEditor(RuntimeEnv.getRuntimeEnv().getMainWind(), properties);
    	fe.setVisible(true);    	
    }
    
    public void move () {
        new PartMover(this, parent);
    }
    
    public void resize () {
    	new PartResizer(this, parent);
    }
    
    public void editScript() {
               
        try {
            String theScript = getProperty(PROP_SCRIPT).toString();        
            ScriptEditor se = new ScriptEditor(RuntimeEnv.getRuntimeEnv().getMainWind(), theScript);
            se.setVisible(true);

            if (!se.wasCanceled())
                setProperty(PROP_SCRIPT, new Value(se.getScript()));
            
        } catch (Exception e) {
            throw new RuntimeException("Unable to set field's script property");
        }
    }
    
    public PartType getType () {
    	return PartType.FIELD;
    }
    
    public String getName() {
    	return properties.getKnownProperty(PROP_NAME).stringValue();
    }
    
    public String getId() {
    	return properties.getKnownProperty(PROP_ID).stringValue();
    }
    
    public JComponent getComponent() {
    	return this;
    }
    
	public void setProperty (String property, Value value) throws NoSuchPropertyException, PropertyPermissionException {
		properties.setProperty(property, value);
	}
	
	public Value getProperty (String property) throws NoSuchPropertyException {
		return properties.getProperty(property);
	}
	
	public Properties getProperties () {
		return properties;
	}
	
	public void setValue(Value value) {
		try {
			properties.setProperty(PROP_TEXT, value);	
		} catch (Exception e) {
			throw new RuntimeException("Field's text property cannot be set");
		}
	}
	
	public Value getValue () {
		return new Value(text.getText());
	}
	
	private void compile() throws HtSemanticException {
						
		try {
			String scriptText = properties.getProperty(PROP_SCRIPT).toString();            
			script = Interpreter.compile(scriptText);			
		} catch (NoSuchPropertyException e) {
			throw new RuntimeException("Field doesn't contain a script");
		} catch (Exception e) {
			throw new HtSemanticException(e.getMessage());
		}		
	}
	
	public void sendMessage (String message) {
		if (!GlobalContext.getContext().noMessages()) {
			GlobalContext.getContext().setMe(new PartIdSpecifier(PartType.FIELD, getId()));
			script.executeHandler(message);
		}
	}
	
	public Value executeUserFunction (String function, ArgumentList arguments) throws HtSemanticException {
		return script.executeUserFunction(function, arguments);		
	}
	
    public JTextArea getTextArea () {
        return text;
    }
    
    public void mousePressed(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {    		
    		RuntimeEnv.getRuntimeEnv().setTheMouse(true);

    		if (!GlobalContext.getContext().noMessages()) 
    			sendMessage("mouseDown");
    	}
    }

    public void mouseReleased(MouseEvent e) {
    	if (SwingUtilities.isLeftMouseButton(e)) {    		
    		RuntimeEnv.getRuntimeEnv().setTheMouse(false);

    		if (!GlobalContext.getContext().noMessages()) 
    			sendMessage("mouseUp");    		     
    	}
    }

    public void mouseEntered(MouseEvent e) {
    	if (!GlobalContext.getContext().noMessages())
    		sendMessage("mouseEnter");    	    
    }

    public void mouseExited(MouseEvent e) {
    	if (!GlobalContext.getContext().noMessages())
    		sendMessage("mouseExit");        
    }
    
    public void keyPressed(KeyEvent e) {
    	if (!GlobalContext.getContext().noMessages())
    		sendMessage("keyDown");    	
    }
    
    public void keyReleased(KeyEvent e) {
    	setValue(new Value(text.getText()));
    	if (!GlobalContext.getContext().noMessages())
    		sendMessage("keyUp");
    }

    public void mouseClicked(MouseEvent e) {}        
    public void keyTyped(KeyEvent e) {}
            
    public void propertyChanged (PartSpecifier part, String property, Value oldValue, Value newValue) {
        
        if (property.equals(PROP_SCRIPT)) {
    		try {
    			compile();
    		} catch (HtSemanticException e) {
    			RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
    		}
    	}
    	
    	else if (property.equals(PROP_TEXT))
    		text.setText(newValue.toString());
    	
    	else if (property.equals(PROP_TOP)		||
    			 property.equals(PROP_LEFT) 	||
    			 property.equals(PROP_WIDTH)   ||
    			 property.equals(PROP_HEIGHT))
    	{
            this.setBounds(getRect());
            this.validate();
            this.repaint();
    	}
        
    	else if (property.equals(PROP_VISIBLE))
    		this.setVisible(newValue.booleanValue());
        
    	else if (property.equals(PROP_WRAPTEXT))
    		text.setLineWrap(newValue.booleanValue());
        
    	else if (property.equals(PROP_LOCKTEXT)) 
    		text.setEditable(!newValue.booleanValue());
    }    
    
    public Rectangle getRect () {
    	try {
    		Rectangle rect = new Rectangle();
    		rect.x = properties.getProperty(PROP_LEFT).integerValue();
    		rect.y = properties.getProperty(PROP_TOP).integerValue();
    		rect.height = properties.getProperty(PROP_HEIGHT).integerValue();
    		rect.width = properties.getProperty(PROP_WIDTH).integerValue();
    		
    		return rect;
    	} catch (Exception e) {
    		throw new RuntimeException("Couldn't get geometry properties for field");
    	}
    }    
}
