/**
 * FieldPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a HyperCard field part user interface by extending the Swing
 * scroll panel.
 */

package hypercard.parts;

import hypercard.context.GlobalContext;
import hypercard.gui.menu.context.FieldContextMenu;
import hypercard.gui.window.FieldPropertyEditor;
import hypercard.gui.window.ScriptEditor;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.model.PartModel;
import hypercard.runtime.Interpreter;
import hypercard.runtime.RuntimeEnv;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Script;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;
import hypercard.parts.model.PartModelObserver;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class FieldPart extends JScrollPane implements Part, MouseListener, PartModelObserver, KeyListener {

    public static final int DEFAULT_WIDTH = 250;
    public static final int DEFAULT_HEIGHT = 100;

    public static final String PROP_SCRIPT = "script";
    public static final String PROP_TEXT = "text";
    public static final String PROP_ID = "id";
    public static final String PROP_NAME = "name";
    public static final String PROP_LEFT = "left";
    public static final String PROP_TOP = "top";
    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";
    public static final String PROP_WRAPTEXT = "wraptext";
    public static final String PROP_VISIBLE = "visible";
    public static final String PROP_LOCKTEXT = "locktext";

    private JTextArea text;
    private Script script;
    private PartModel partModel;
    private CardPart parent;

    private FieldPart(CardPart parent) {
        super();

        this.parent = parent;
        this.script = new Script();

        initComponents();
    }

    public static FieldPart fromGeometry (CardPart parent, Rectangle geometry) {
        FieldPart field = new FieldPart(parent);

        field.initProperties(geometry);

        return field;
    }

    public static FieldPart fromModel (CardPart parent, PartModel model) throws Exception {
        FieldPart field = new FieldPart(parent);

        field.partModel = model;
        field.partModel.addModelChangeListener(field);

        field.text.setText(model.getKnownProperty(PROP_TEXT).stringValue());
        field.script = Interpreter.compile(model.getKnownProperty(PROP_SCRIPT).stringValue());

        return field;
    }

    @Override
    public void partOpened() {
        this.setComponentPopupMenu(new FieldContextMenu(this));
    }

    private void initProperties(Rectangle geometry) {
        int id = parent.nextFieldId();

        partModel = PartModel.newPartOfType(PartType.FIELD);
        partModel.addModelChangeListener(this);

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_TEXT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("Text Field " + id), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_WRAPTEXT, new Value(true), false);
        partModel.defineProperty(PROP_VISIBLE, new Value(true), false);
        partModel.defineProperty(PROP_LOCKTEXT, new Value(false), false);
    }

    private void initComponents() {
        text = new JTextArea();
        text.setTabSize(4);
        text.setComponentPopupMenu(new FieldContextMenu(this));
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.addMouseListener(this);
        text.addKeyListener(this);
        this.setViewportView(text);
    }

    public void editProperties() {
        WindowBuilder.make(new FieldPropertyEditor())
                .withTitle("Properties of field " + getName())
                .withModel(partModel)
                .withLocationRelativeTo(RuntimeEnv.getRuntimeEnv().getStackPanel())
                .resizeable(false)
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
                .withTitle("Script of field " + getName())
                .withModel(partModel)
                .withLocationRelativeTo(RuntimeEnv.getRuntimeEnv().getStackPanel())
                .resizeable(true)
                .build();
    }

    @Override
    public PartType getType() {
        return PartType.FIELD;
    }

    @Override
    public String getName() {
        return partModel.getKnownProperty(PROP_NAME).stringValue();
    }

    @Override
    public int getId() {
        return partModel.getKnownProperty(PROP_ID).integerValue();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setProperty(String property, Value value) throws NoSuchPropertyException, PropertyPermissionException {
        partModel.setProperty(property, value);
    }

    @Override
    public Value getProperty(String property) throws NoSuchPropertyException {
        return partModel.getProperty(property);
    }

    public PartModel getPartModel() {
        return partModel;
    }

    @Override
    public void setValue(Value value) {
        try {
            partModel.setProperty(PROP_TEXT, value);
        } catch (Exception e) {
            throw new RuntimeException("Field's text property cannot be set");
        }
    }

    @Override
    public Value getValue() {
        return new Value(text.getText());
    }

    private void compile() throws HtSemanticException {

        try {
            script = Interpreter.compile(partModel.getProperty(PROP_SCRIPT).toString());
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("Field doesn't contain a script");
        } catch (Exception e) {
            throw new HtSemanticException(e.getMessage());
        }
    }

    @Override
    public void sendMessage(String message) {
        if (!GlobalContext.getContext().noMessages()) {
            GlobalContext.getContext().setMe(new PartIdSpecifier(PartType.FIELD, getId()));
            script.executeHandler(message);
        }
    }

    @Override
    public Value executeUserFunction(String function, ArgumentList arguments) throws HtSemanticException {
        return script.executeUserFunction(function, arguments);
    }

    public JTextArea getTextArea() {
        return text;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            RuntimeEnv.getRuntimeEnv().setTheMouse(true);

            if (!GlobalContext.getContext().noMessages())
                sendMessage("mouseDown");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            RuntimeEnv.getRuntimeEnv().setTheMouse(false);

            if (!GlobalContext.getContext().noMessages())
                sendMessage("mouseUp");
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!GlobalContext.getContext().noMessages())
            sendMessage("mouseEnter");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!GlobalContext.getContext().noMessages())
            sendMessage("mouseExit");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!GlobalContext.getContext().noMessages())
            sendMessage("keyDown");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (!GlobalContext.getContext().noMessages())
            sendMessage("keyUp");

        partModel.setKnownProperty(PROP_TEXT, new Value(getTextArea().getText()));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void onModelChange(String property, Value oldValue, Value newValue) {

        if (property.equals(PROP_SCRIPT)) {
            try {
                compile();
            } catch (HtSemanticException e) {
                RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
            }
        } else if (property.equals(PROP_TEXT) && !newValue.toString().equals(text.getText()))
            text.setText(newValue.toString());

        else if (property.equals(PROP_TOP) ||
                property.equals(PROP_LEFT) ||
                property.equals(PROP_WIDTH) ||
                property.equals(PROP_HEIGHT)) {
            this.setBounds(getRect());
            this.validate();
            this.repaint();
        } else if (property.equals(PROP_VISIBLE))
            this.setVisible(newValue.booleanValue());

        else if (property.equals(PROP_WRAPTEXT))
            text.setLineWrap(newValue.booleanValue());

        else if (property.equals(PROP_LOCKTEXT))
            text.setEditable(!newValue.booleanValue());
    }

    public Rectangle getRect() {
        try {
            Rectangle rect = new Rectangle();
            rect.x = partModel.getProperty(PROP_LEFT).integerValue();
            rect.y = partModel.getProperty(PROP_TOP).integerValue();
            rect.height = partModel.getProperty(PROP_HEIGHT).integerValue();
            rect.width = partModel.getProperty(PROP_WIDTH).integerValue();

            return rect;
        } catch (Exception e) {
            throw new RuntimeException("Couldn't get geometry partModel for field");
        }
    }
}
