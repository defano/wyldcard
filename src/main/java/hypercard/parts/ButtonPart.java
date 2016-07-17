/**
 * ButtonPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements the user interface for a HyperCard button part by extending the
 * Swing push button class.
 */

package hypercard.parts;

import hypercard.context.GlobalContext;
import hypercard.gui.menu.ButtonContextMenu;
import hypercard.gui.window.ButtonPropertyEditor;
import hypercard.gui.window.ScriptEditor;
import hypercard.gui.window.WindowBuilder;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class ButtonPart extends JButton implements Part, MouseListener, PropertyChangeListener, Serializable {
    private static final long serialVersionUID = 6441731559079090863L;

    public static final int DEFAULT_WIDTH = 85;
    public static final int DEFAULT_HEIGHT = 25;

    public static final String PROP_SCRIPT = "script";
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_TITLE = "title";
    public static final String PROP_LEFT = "left";
    public static final String PROP_TOP = "top";
    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";
    public static final String PROP_SHOWTITLE = "showtitle";
    public static final String PROP_VISIBLE = "visible";
    public static final String PROP_ENABLED = "enabled";

    private Script script;
    private Properties properties;
    private CardPart parent;

    public ButtonPart(Rectangle geometry, CardPart parent) {
        super();

        this.parent = parent;
        this.script = new Script();

        initComponents();
        initProperties(geometry);

        this.setValue(properties.getKnownProperty(PROP_TITLE));
    }

    public void partOpened() {
        this.setComponentPopupMenu(new ButtonContextMenu(this));
    }

    private void initProperties(Rectangle geometry) {
        String id = String.valueOf(parent.getNextPartId());

        properties = new Properties(this);
        properties.addPropertyChangeListener(this);

        properties.defineProperty(PROP_SCRIPT, new Value(), false);
        properties.defineProperty(PROP_ID, new Value(id), true);
        properties.defineProperty(PROP_NAME, new Value("Button " + id), false);
        properties.defineProperty(PROP_TITLE, new Value("Button"), false);
        properties.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        properties.defineProperty(PROP_TOP, new Value(geometry.y), false);
        properties.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        properties.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        properties.defineProperty(PROP_SHOWTITLE, new Value(true), false);
        properties.defineProperty(PROP_VISIBLE, new Value(true), false);
        properties.defineProperty(PROP_ENABLED, new Value(true), false);
    }

    private void initComponents() {
        this.addMouseListener(this);
        this.setComponentPopupMenu(new ButtonContextMenu(this));
    }

    public void editProperties() {
        WindowBuilder.make(new ButtonPropertyEditor())
                .withTitle("Button Properties")
                .withModel(properties)
                .withLocationRelativeTo(RuntimeEnv.getRuntimeEnv().getCardPanel())
                .build();
    }

    public void move() {
        new PartMover(this, parent);
    }

    public void resize() {
        new PartResizer(this, parent);
    }

    public void editScript() {
        WindowBuilder.make(new ScriptEditor())
                .withTitle("HyperTalk Script Editor")
                .withModel(properties)
                .withLocationRelativeTo(RuntimeEnv.getRuntimeEnv().getCardPanel())
                .resizeable(true)
                .build();
    }

    public PartType getType() {
        return PartType.BUTTON;
    }

    public String getName() {
        return properties.getKnownProperty(PROP_NAME).stringValue();
    }

    public String getId() {
        return properties.getKnownProperty(PROP_ID).stringValue();
    }

    public PartSpecifier getPartSpecifier() {
        return new PartIdSpecifier(PartType.BUTTON, getId());
    }

    public JComponent getComponent() {
        return this;
    }

    public void setProperty(String property, Value value) throws NoSuchPropertyException, PropertyPermissionException {
        properties.setProperty(property, value);
    }

    public Value getProperty(String property) throws NoSuchPropertyException {
        return properties.getProperty(property);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setValue(Value value) {
        try {
            properties.setProperty(PROP_TITLE, value);
        } catch (Exception e) {
            throw new RuntimeException("Button's text property cannot be set");
        }
    }

    public Value getValue() {
        return properties.getKnownProperty(PROP_TITLE);
    }

    private void compile() throws HtSemanticException {

        try {
            String scriptText = properties.getProperty(PROP_SCRIPT).toString();
            script = Interpreter.compile(scriptText);
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("Button doesn't contain a script");
        } catch (Exception e) {
            throw new HtSemanticException(e.getMessage());
        }
    }

    public void sendMessage(String message) {
        GlobalContext.getContext().setMe(new PartIdSpecifier(PartType.BUTTON, getId()));
        script.executeHandler(message);
    }

    public Value executeUserFunction(String function, ArgumentList arguments) throws HtSemanticException {
        return script.executeUserFunction(function, arguments);
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

    public void mouseClicked(MouseEvent e) {
    }

    public void propertyChanged(PartSpecifier part, String property, Value oldValue, Value newValue) {

        if (property.equals(PROP_SCRIPT)) {
            try {
                compile();
            } catch (HtSemanticException e) {
                RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
            }
        } else if (property.equals(PROP_TITLE))
            this.setText(newValue.toString());

        else if (property.equals(PROP_TOP) ||
                property.equals(PROP_LEFT) ||
                property.equals(PROP_WIDTH) ||
                property.equals(PROP_HEIGHT)) {
            this.setBounds(getRect());
            this.validate();
            this.repaint();
        } else if (property.equals(PROP_VISIBLE))
            this.setVisible(newValue.booleanValue());

        else if (property.equals(PROP_ENABLED))
            this.setEnabled(newValue.booleanValue());

        else if (property.equals(PROP_SHOWTITLE)) {
            if (newValue.booleanValue())
                this.setText(properties.getKnownProperty(PROP_TITLE).stringValue());
            else
                this.setText("");
        }
    }

    public Rectangle getRect() {
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
