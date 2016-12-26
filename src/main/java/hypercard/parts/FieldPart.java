/**
 * FieldPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a HyperCard field part user interface by extending the Swing
 * scroll panel.
 */

package hypercard.parts;

import hypercard.gui.menu.context.FieldContextMenu;
import hypercard.gui.window.FieldPropertyEditor;
import hypercard.gui.window.ScriptEditor;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.model.FieldModel;
import hypercard.parts.model.AbstractPartModel;
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
import hypercard.parts.model.PropertyChangeObserver;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class FieldPart extends JScrollPane implements Part, MouseListener, PropertyChangeObserver, KeyListener {

    public static final int DEFAULT_WIDTH = 250;
    public static final int DEFAULT_HEIGHT = 100;

    private JTextArea text;
    private Script script;
    private FieldModel partModel;
    private CardPart parent;

    private FieldPart(CardPart parent) {
        super();

        this.parent = parent;
        this.script = new Script();
        initComponents();
    }

    public static FieldPart newField(CardPart parent) {
        // Center new field in card
        return fromGeometry(parent, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public static FieldPart fromGeometry(CardPart parent, Rectangle geometry) {
        FieldPart field = new FieldPart(parent);

        field.initProperties(geometry);

        return field;
    }

    public static FieldPart fromModel(CardPart parent, FieldModel model) throws Exception {
        FieldPart field = new FieldPart(parent);

        field.partModel = model;
        field.partModel.addPropertyChangedObserver(field);

        field.text.setText(model.getKnownProperty(FieldModel.PROP_TEXT).stringValue());
        field.script = Interpreter.compile(model.getKnownProperty(FieldModel.PROP_SCRIPT).stringValue());

        return field;
    }

    @Override
    public void partOpened() {
        this.setComponentPopupMenu(new FieldContextMenu(this));
    }

    @Override
    public Rectangle getRect() {
        return partModel.getRect();
    }

    private void initProperties(Rectangle geometry) {
        int id = parent.nextFieldId();

        partModel = FieldModel.newFieldModel(id, geometry);
        partModel.addPropertyChangedObserver(this);
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
        return partModel.getKnownProperty(FieldModel.PROP_NAME).stringValue();
    }

    @Override
    public int getId() {
        return partModel.getKnownProperty(FieldModel.PROP_ID).integerValue();
    }

    @Override
    public JComponent getComponent() {
        return text;
    }

    @Override
    public void setProperty(String property, Value value) throws NoSuchPropertyException, PropertyPermissionException, HtSemanticException {
        partModel.setProperty(property, value);
    }

    @Override
    public Value getProperty(String property) throws NoSuchPropertyException {
        return partModel.getProperty(property);
    }

    public AbstractPartModel getPartModel() {
        return partModel;
    }

    @Override
    public void setValue(Value value) {
        try {
            partModel.setProperty(FieldModel.PROP_TEXT, value);
        } catch (Exception e) {
            throw new RuntimeException("Field's text property cannot be set");
        }
    }

    @Override
    public Value getValue() {
        return partModel.getKnownProperty(FieldModel.PROP_TEXT);
    }

    private void compile() throws HtSemanticException {

        try {
            script = Interpreter.compile(partModel.getProperty(FieldModel.PROP_SCRIPT).toString());
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("Field doesn't contain a script");
        } catch (Exception e) {
            throw new HtSemanticException(e.getMessage());
        }
    }

    @Override
    public void sendMessage(String message) {
        RuntimeEnv.getRuntimeEnv().executeHandler(getMe(), script, message, true);
    }

    @Override
    public Value executeUserFunction(String function, ArgumentList arguments) throws HtSemanticException {
        return RuntimeEnv.getRuntimeEnv().executeUserFunction(getMe(), script.getFunction(function), arguments, false);
    }

    public PartSpecifier getMe() {
        return new PartIdSpecifier(PartType.FIELD, getId());
    }

    public JTextArea getTextArea() {
        return text;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            RuntimeEnv.getRuntimeEnv().setTheMouse(true);
            sendMessage("mouseDown");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            RuntimeEnv.getRuntimeEnv().setTheMouse(false);
            sendMessage("mouseUp");
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        sendMessage("mouseEnter");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        sendMessage("mouseExit");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        sendMessage("keyDown");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        sendMessage("keyUp");

        partModel.setKnownProperty(FieldModel.PROP_TEXT, new Value(getTextArea().getText()));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {

        SwingUtilities.invokeLater(() -> {
            switch (property) {
                case FieldModel.PROP_SCRIPT:
                    try {
                        compile();
                    } catch (HtSemanticException e) {
                        RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
                    }
                    break;
                case FieldModel.PROP_TEXT:
                    if (!newValue.toString().equals(text.getText())) {
                        text.setText(newValue.toString());
                    }
                    break;
                case FieldModel.PROP_TOP:
                case FieldModel.PROP_LEFT:
                case FieldModel.PROP_WIDTH:
                case FieldModel.PROP_HEIGHT:
                    setBounds(partModel.getRect());
                    validate();
                    repaint();
                    break;
                case FieldModel.PROP_VISIBLE:
                    setVisible(newValue.booleanValue());
                    break;
                case FieldModel.PROP_WRAPTEXT:
                    text.setLineWrap(newValue.booleanValue());
                    break;
                case FieldModel.PROP_LOCKTEXT:
                    text.setEditable(!newValue.booleanValue());
                    break;
            }
        });
    }
}
