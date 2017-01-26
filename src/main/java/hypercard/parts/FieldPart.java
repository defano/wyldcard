/**
 * FieldPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a HyperCard field part user interface by extending the Swing
 * scroll panel.
 */

package hypercard.parts;

import hypercard.HyperCard;
import hypercard.context.PartToolContext;
import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.gui.window.FieldPropertyEditor;
import hypercard.gui.window.ScriptEditor;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.fields.AbstractField;
import hypercard.parts.fields.FieldStyle;
import hypercard.parts.model.AbstractPartModel;
import hypercard.parts.model.FieldModel;
import hypercard.parts.model.PropertyChangeObserver;
import hypercard.runtime.Interpreter;
import hypercard.runtime.WindowManager;
import hypertalk.ast.common.ExpressionList;
import hypertalk.ast.common.PartType;
import hypertalk.ast.common.Script;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartIdSpecifier;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FieldPart extends AbstractField implements Part, MouseListener, PropertyChangeObserver, KeyListener {

    public static final int DEFAULT_WIDTH = 250;
    public static final int DEFAULT_HEIGHT = 100;

    private Script script;
    private FieldModel partModel;
    private CardPart parent;
    private PartMover mover;

    private FieldPart(FieldStyle style, CardPart parent) {
        super(style);

        this.mover = new PartMover(this, parent);
        this.parent = parent;
        this.script = new Script();
    }

    public static FieldPart newField(CardPart parent) {
        FieldPart newField = fromGeometry(parent, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT));

        // When a new field is created, make the field tool active and select the newly created part
        ToolsContext.getInstance().setToolMode(ToolMode.FIELD);
        PartToolContext.getInstance().setSelectedPart(newField);

        return newField;
    }

    public static FieldPart fromGeometry(CardPart parent, Rectangle geometry) {
        FieldPart field = new FieldPart(FieldStyle.OPAQUE, parent);

        field.initProperties(geometry);

        return field;
    }

    public static FieldPart fromModel(CardPart parent, FieldModel model) throws Exception {
        FieldPart field = new FieldPart(FieldStyle.fromName(model.getKnownProperty(FieldModel.PROP_STYLE).stringValue()), parent);

        field.script = Interpreter.compile(model.getKnownProperty(FieldModel.PROP_SCRIPT).stringValue());
        field.partModel = model;
        field.partModel.addPropertyChangedObserver(field);

        return field;
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

    public void editProperties() {
        WindowBuilder.make(new FieldPropertyEditor())
                .withTitle("Properties of field " + getName())
                .withModel(partModel)
                .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                .resizeable(false)
                .build();
    }

    public void move() {
        if (!mover.isMoving()) {
            mover.startMoving();
        }
    }

    @Override
    public void resize(int fromQuadrant) {
        new PartResizer(this, parent, fromQuadrant);
    }

    @Override
    public void delete() {
        parent.removeField(this);
    }

    @Override
    public void invalidateSwingComponent(Component oldComponent, Component newComponent) {
        parent.invalidateSwingComponent(this, oldComponent, newComponent);
    }

    public void editScript() {
        WindowBuilder.make(new ScriptEditor())
                .withTitle("Script of field " + getName())
                .withModel(partModel)
                .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
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
        return this.getFieldComponent();
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
        Interpreter.executeHandler(getMe(), script, message);
    }

    @Override
    public Value executeUserFunction(String function, ExpressionList arguments) throws HtSemanticException {
        return Interpreter.executeFunction(getMe(), script.getFunction(function), arguments);
    }

    public PartSpecifier getMe() {
        return new PartIdSpecifier(PartType.FIELD, getId());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            sendMessage("mouseDown");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            sendMessage("mouseUp");
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        sendMessage("mouseEnter");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        sendMessage("mouseExit");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        sendMessage("keyDown");
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        sendMessage("keyUp");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {

        SwingUtilities.invokeLater(() -> {
            switch (property) {
                case FieldModel.PROP_STYLE:
                    setFieldStyle(FieldStyle.fromName(newValue.stringValue()));
                    break;
                case FieldModel.PROP_SCRIPT:
                    try {
                        compile();
                    } catch (HtSemanticException e) {
                        HyperCard.getInstance().dialogSyntaxError(e);
                    }
                    break;
                case FieldModel.PROP_TOP:
                case FieldModel.PROP_LEFT:
                case FieldModel.PROP_WIDTH:
                case FieldModel.PROP_HEIGHT:
                    getComponent().setBounds(partModel.getRect());
                    getComponent().validate();
                    getComponent().repaint();
                    break;
                case FieldModel.PROP_VISIBLE:
                    getComponent().setVisible(newValue.booleanValue());
                    break;
            }
        });
    }
}
