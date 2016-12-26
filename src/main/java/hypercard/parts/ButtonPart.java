/**
 * ButtonPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements the user interface for a HyperCard button part by extending the
 * Swing push button class.
 */

package hypercard.parts;

import hypercard.gui.menu.context.ButtonContextMenu;
import hypercard.gui.window.ButtonPropertyEditor;
import hypercard.gui.window.ScriptEditor;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.model.*;
import hypercard.parts.model.ButtonModel;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ButtonPart extends JButton implements Part, MouseListener, PropertyChangeObserver {

    public static final int DEFAULT_WIDTH = 160;
    public static final int DEFAULT_HEIGHT = 40;

    private Script script;
    private ButtonModel partModel;
    private CardPart parent;

    private ButtonPart(CardPart parent) {
        super();

        this.parent = parent;
        this.script = new Script();

        initComponents();
    }

    public static ButtonPart newButton(CardPart parent) {
        return fromGeometry(parent, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public static ButtonPart fromGeometry(CardPart parent, Rectangle geometry) {
        ButtonPart button = new ButtonPart(parent);

        button.initProperties(geometry);
        button.setText(button.partModel.getKnownProperty(ButtonModel.PROP_TITLE).stringValue());

        return button;
    }

    public static ButtonPart fromModel(CardPart parent, ButtonModel partModel) throws Exception {
        ButtonPart button = new ButtonPart(parent);

        button.partModel = partModel;
        button.partModel.addPropertyChangedObserver(button);
        button.script = Interpreter.compile(partModel.getKnownProperty(ButtonModel.PROP_SCRIPT).stringValue());
        button.setText(button.partModel.getKnownProperty(ButtonModel.PROP_TITLE).stringValue());

        return button;
    }

    @Override
    public void partOpened() {
        this.setComponentPopupMenu(new ButtonContextMenu(this));
    }

    @Override
    public Rectangle getRect() {
        return partModel.getRect();
    }

    private void initProperties(Rectangle geometry) {
        int id = parent.nextButtonId();

        partModel = ButtonModel.newButtonModel(id, geometry);
        partModel.addPropertyChangedObserver(this);
    }

    private void initComponents() {
        this.addMouseListener(this);
        this.setComponentPopupMenu(new ButtonContextMenu(this));
    }

    public void editProperties() {
        WindowBuilder.make(new ButtonPropertyEditor())
                .withTitle("Button PartModel")
                .withModel(partModel)
                .withLocationRelativeTo(RuntimeEnv.getRuntimeEnv().getStackPanel())
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
                .withModel(partModel)
                .withLocationRelativeTo(RuntimeEnv.getRuntimeEnv().getStackPanel())
                .resizeable(true)
                .build();
    }

    @Override
    public PartType getType() {
        return PartType.BUTTON;
    }

    @Override
    public String getName() {
        return partModel.getKnownProperty(ButtonModel.PROP_NAME).stringValue();
    }

    @Override
    public int getId() {
        return partModel.getKnownProperty(ButtonModel.PROP_ID).integerValue();
    }

    @Override
    public JComponent getComponent() {
        return this;
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
            partModel.setProperty(ButtonModel.PROP_TITLE, value);
        } catch (Exception e) {
            throw new RuntimeException("Button's text property cannot be set");
        }
    }

    @Override
    public Value getValue() {
        return partModel.getKnownProperty(ButtonModel.PROP_TITLE);
    }

    private void compile() throws HtSemanticException {

        try {
            String scriptText = partModel.getProperty(ButtonModel.PROP_SCRIPT).toString();
            script = Interpreter.compile(scriptText);
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("Button doesn't contain a script");
        } catch (Exception e) {
            throw new HtSemanticException(e.getMessage());
        }
    }

    public PartSpecifier getMe() {
        return new PartIdSpecifier(PartType.BUTTON, getId());
    }

    @Override
    public void sendMessage(String message) {
        RuntimeEnv.getRuntimeEnv().executeHandler(getMe(), script, message, true);
    }

    @Override
    public Value executeUserFunction(String function, ArgumentList arguments) throws HtSemanticException {
        return RuntimeEnv.getRuntimeEnv().executeUserFunction(getMe(), script.getFunction(function), arguments, false);
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
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {

        SwingUtilities.invokeLater(() -> {
            switch (property) {
                case ButtonModel.PROP_SCRIPT:
                    try {
                        compile();
                    } catch (HtSemanticException e) {
                        RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
                    }
                    break;
                case ButtonModel.PROP_TITLE:
                    setText(newValue.toString());
                    break;
                case ButtonModel.PROP_TOP:
                case ButtonModel.PROP_LEFT:
                case ButtonModel.PROP_WIDTH:
                case ButtonModel.PROP_HEIGHT:
                    setBounds(partModel.getRect());
                    validate();
                    repaint();
                    break;
                case ButtonModel.PROP_VISIBLE:
                    setVisible(newValue.booleanValue());
                    break;
                case ButtonModel.PROP_ENABLED:
                    setEnabled(newValue.booleanValue());
                    break;
                case ButtonModel.PROP_SHOWTITLE:
                    if (newValue.booleanValue())
                        setText(partModel.getKnownProperty(ButtonModel.PROP_TITLE).stringValue());
                    else
                        setText("");
                    break;
            }
        });
    }
}
