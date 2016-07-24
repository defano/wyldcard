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
import hypercard.gui.menu.context.ButtonContextMenu;
import hypercard.gui.window.ButtonPropertyEditor;
import hypercard.gui.window.ScriptEditor;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.model.PartModel;
import hypercard.parts.model.PartModelObserver;
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

public class ButtonPart extends JButton implements Part, MouseListener, PartModelObserver {

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
    private PartModel partModel;
    private CardPart parent;

    private ButtonPart(CardPart parent) {
        super();

        this.parent = parent;
        this.script = new Script();

        initComponents();
    }

    public static ButtonPart fromGeometry(CardPart parent, Rectangle geometry) {
        ButtonPart button = new ButtonPart(parent);

        button.initProperties(geometry);
        button.setText(button.partModel.getKnownProperty(PROP_TITLE).stringValue());

        return button;
    }

    public static ButtonPart fromModel(CardPart parent, PartModel partModel) throws Exception {
        ButtonPart button = new ButtonPart(parent);

        button.partModel = partModel;
        button.partModel.addModelChangeListener(button);
        button.script = Interpreter.compile(partModel.getKnownProperty(PROP_SCRIPT).stringValue());
        button.setText(button.partModel.getKnownProperty(PROP_TITLE).stringValue());

        return button;
    }

    @Override
    public void partOpened() {
        this.setComponentPopupMenu(new ButtonContextMenu(this));
    }

    private void initProperties(Rectangle geometry) {
        int id = parent.nextButtonId();

        partModel = PartModel.newPartOfType(PartType.BUTTON);
        partModel.addModelChangeListener(this);

        partModel.defineProperty(PROP_SCRIPT, new Value(), false);
        partModel.defineProperty(PROP_ID, new Value(id), true);
        partModel.defineProperty(PROP_NAME, new Value("Button " + id), false);
        partModel.defineProperty(PROP_TITLE, new Value("Button"), false);
        partModel.defineProperty(PROP_LEFT, new Value(geometry.x), false);
        partModel.defineProperty(PROP_TOP, new Value(geometry.y), false);
        partModel.defineProperty(PROP_WIDTH, new Value(geometry.width), false);
        partModel.defineProperty(PROP_HEIGHT, new Value(geometry.height), false);
        partModel.defineProperty(PROP_SHOWTITLE, new Value(true), false);
        partModel.defineProperty(PROP_VISIBLE, new Value(true), false);
        partModel.defineProperty(PROP_ENABLED, new Value(true), false);
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
        return partModel.getKnownProperty(PROP_NAME).stringValue();
    }

    @Override
    public int getId() {
        return partModel.getKnownProperty(PROP_ID).integerValue();
    }

    public PartSpecifier getPartSpecifier() {
        return new PartIdSpecifier(PartType.BUTTON, getId());
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
            partModel.setProperty(PROP_TITLE, value);
        } catch (Exception e) {
            throw new RuntimeException("Button's text property cannot be set");
        }
    }

    @Override
    public Value getValue() {
        return partModel.getKnownProperty(PROP_TITLE);
    }

    private void compile() throws HtSemanticException {

        try {
            String scriptText = partModel.getProperty(PROP_SCRIPT).toString();
            script = Interpreter.compile(scriptText);
        } catch (NoSuchPropertyException e) {
            throw new RuntimeException("Button doesn't contain a script");
        } catch (Exception e) {
            throw new HtSemanticException(e.getMessage());
        }
    }

    @Override
    public void sendMessage(String message) {
        GlobalContext.getContext().setMe(new PartIdSpecifier(PartType.BUTTON, getId()));
        script.executeHandler(message);
    }

    @Override
    public Value executeUserFunction(String function, ArgumentList arguments) throws HtSemanticException {
        return script.executeUserFunction(function, arguments);
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
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void onModelChange(String property, Value oldValue, Value newValue) {

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
                this.setText(partModel.getKnownProperty(PROP_TITLE).stringValue());
            else
                this.setText("");
        }
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
