/**
 * ButtonPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements the user interface for a HyperCard button part by extending the
 * Swing push button class.
 */

package hypercard.parts;

import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.gui.menu.context.ButtonContextMenu;
import hypercard.gui.window.ButtonPropertyEditor;
import hypercard.gui.window.ScriptEditor;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.buttons.ButtonStyle;
import hypercard.parts.buttons.HyperCardButton;
import hypercard.parts.model.*;
import hypercard.parts.model.ButtonModel;
import hypercard.runtime.Interpreter;
import hypercard.HyperCard;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ButtonPart extends HyperCardButton implements Part, MouseListener, PropertyChangeObserver {

    public static final int DEFAULT_WIDTH = 160;
    public static final int DEFAULT_HEIGHT = 40;

    private final PartMover mover;
    private Script script;
    private ButtonModel partModel;
    private CardPart parent;

    private ButtonPart(ButtonStyle style, CardPart parent) {
        super(style);

        this.parent = parent;
        this.script = new Script();
        this.mover = new PartMover(this, parent, true);
    }

    public static ButtonPart newButton(CardPart parent) {
        return fromGeometry(parent, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public static ButtonPart fromGeometry(CardPart parent, Rectangle geometry) {
        ButtonPart button = new ButtonPart(ButtonStyle.DEFAULT, parent);

        button.initProperties(geometry);
        button.setName(button.partModel.getKnownProperty(ButtonModel.PROP_NAME).stringValue());

        return button;
    }

    public static ButtonPart fromModel(CardPart parent, ButtonModel partModel) throws Exception {
        ButtonStyle style = ButtonStyle.fromName(partModel.getKnownProperty(ButtonModel.PROP_STYLE).stringValue());
        ButtonPart button = new ButtonPart(style, parent);

        button.partModel = partModel;
        button.partModel.addPropertyChangedObserver(button);
        button.script = Interpreter.compile(partModel.getKnownProperty(ButtonModel.PROP_SCRIPT).stringValue());
        button.setName(button.partModel.getKnownProperty(ButtonModel.PROP_NAME).stringValue());

        return button;
    }

    @Override
    public void partOpened() {
        this.getButtonComponent().setComponentPopupMenu(new ButtonContextMenu(this));
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

    @Override
    public void editProperties() {
        WindowBuilder.make(new ButtonPropertyEditor())
                .withTitle("Button Editor")
                .withModel(partModel)
                .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                .build();
    }

    @Override
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
        parent.removeButton(this);
    }

    /**
     * Indicates that the Swing component associated with this {@link ButtonPart} has changed and that the button's
     * parent (i.e., card or background) should update itself accordingly.
     *
     * @param oldButtonComponent The former component associated with this part
     * @param newButtonComponent The new component
     */
    public void invalidateButtonComponent(Component oldButtonComponent, Component newButtonComponent) {
        parent.invalidateButtonComponent(this, oldButtonComponent, newButtonComponent);
    }

    public void editScript() {
        WindowBuilder.make(new ScriptEditor())
                .withTitle("HyperTalk Script Editor")
                .withModel(partModel)
                .withLocationCenteredOver(WindowManager.getStackWindow().getWindowPanel())
                .resizeable(true)
                .build();
    }

    @Override
    public AbstractPartModel getModel() {
        return partModel;
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
        return this.getButtonComponent();
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
            partModel.setProperty(ButtonModel.PROP_NAME, value);
        } catch (Exception e) {
            throw new RuntimeException("Button's text property cannot be set");
        }
    }

    @Override
    public Value getValue() {
        return partModel.getKnownProperty(ButtonModel.PROP_NAME);
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
        Interpreter.executeHandler(getMe(), script, message);
    }

    @Override
    public Value executeUserFunction(String function, ExpressionList arguments) throws HtSemanticException {
        return Interpreter.executeFunction(getMe(), script.getFunction(function), arguments);
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

        if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE && SwingUtilities.isLeftMouseButton(e)) {
            sendMessage("mouseUp");
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);

        if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE) {
            sendMessage("mouseEnter");
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);

        if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE) {
            sendMessage("mouseExit");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void onPropertyChanged(String property, Value oldValue, Value newValue) {

        SwingUtilities.invokeLater(() -> {
            switch (property) {
                case ButtonModel.PROP_STYLE:
                    setButtonStyle(ButtonStyle.fromName(newValue.stringValue()));
                    break;
                case ButtonModel.PROP_SCRIPT:
                    try {
                        compile();
                    } catch (HtSemanticException e) {
                        HyperCard.getInstance().dialogSyntaxError(e);
                    }
                    break;
                case ButtonModel.PROP_NAME:
                    setName(newValue.toString());
                    break;
                case ButtonModel.PROP_TOP:
                case ButtonModel.PROP_LEFT:
                case ButtonModel.PROP_WIDTH:
                case ButtonModel.PROP_HEIGHT:
                    getButtonComponent().setBounds(partModel.getRect());
                    getButtonComponent().validate();
                    getButtonComponent().repaint();
                    break;
                case ButtonModel.PROP_VISIBLE:
                    getButtonComponent().setVisible(newValue.booleanValue());
                    break;
                case ButtonModel.PROP_ENABLED:
                    getButtonComponent().setEnabled(newValue.booleanValue());
                    break;
                case ButtonModel.PROP_SHOWNAME:
                    if (newValue.booleanValue())
                        setName(partModel.getKnownProperty(ButtonModel.PROP_NAME).stringValue());
                    else
                        setName("");
                    break;
            }
        });
    }
}
