/**
 * ButtonPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements the user interface for a HyperCard button part by extending the
 * Swing push button class.
 */

package hypercard.parts;

import hypercard.context.PartToolContext;
import hypercard.context.ToolMode;
import hypercard.context.ToolsContext;
import hypercard.gui.window.ButtonPropertyEditor;
import hypercard.gui.window.WindowBuilder;
import hypercard.parts.buttons.ButtonStyle;
import hypercard.parts.buttons.AbstractStylableButton;
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
import hypertalk.exception.HtSemanticException;
import hypertalk.exception.NoSuchPropertyException;
import hypertalk.exception.PropertyPermissionException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ButtonPart extends AbstractStylableButton implements MouseListener, PropertyChangeObserver {

    private static final int DEFAULT_WIDTH = 160;
    private static final int DEFAULT_HEIGHT = 40;

    private final PartMover mover;
    private Script script;
    private ButtonModel partModel;
    private CardPart parent;

    private ButtonPart(ButtonStyle style, CardPart parent) {
        super(style);

        this.parent = parent;
        this.script = new Script();
        this.mover = new PartMover(this, parent);
    }

    public static ButtonPart newButton(CardPart parent) {
        ButtonPart newButton = fromGeometry(parent, new Rectangle(parent.getWidth() / 2 - (DEFAULT_WIDTH / 2), parent.getHeight() / 2 - (DEFAULT_HEIGHT / 2), DEFAULT_WIDTH, DEFAULT_HEIGHT));

        // When a new button is created, make the button tool active and select the newly created button
        ToolsContext.getInstance().setToolMode(ToolMode.BUTTON);
        PartToolContext.getInstance().setSelectedPart(newButton);

        return newButton;
    }

    public static ButtonPart fromGeometry(CardPart parent, Rectangle geometry) {
        ButtonPart button = new ButtonPart(ButtonStyle.DEFAULT, parent);
        button.initProperties(geometry);
        return button;
    }

    public static ButtonPart fromModel(CardPart parent, ButtonModel partModel) throws Exception {
        ButtonStyle style = ButtonStyle.fromName(partModel.getKnownProperty(ButtonModel.PROP_STYLE).stringValue());
        ButtonPart button = new ButtonPart(style, parent);

        button.partModel = partModel;
        button.partModel.addPropertyChangedObserver(button);
        button.script = Interpreter.compile(partModel.getKnownProperty(ButtonModel.PROP_SCRIPT).stringValue());

        return button;
    }

    @Override
    public void partOpened() {
        super.partOpened();
    }

    @Override
    public void partClosed() {
        super.partClosed();
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
    public CardPart getParentCard() {
        return parent;
    }

    @Override
    public Part getPart() {
        return this;
    }

    @Override
    public void move() {
        mover.startMoving();
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
    @Override
    public void invalidateSwingComponent(Component oldButtonComponent, Component newButtonComponent) {
        parent.invalidateSwingComponent(this, oldButtonComponent, newButtonComponent);
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
            partModel.setProperty(ButtonModel.PROP_CONTENTS, value);
        } catch (Exception e) {
            throw new RuntimeException("Button's text property cannot be set");
        }
    }

    @Override
    public Value getValue() {
        return partModel.getKnownProperty(ButtonModel.PROP_CONTENTS);
    }

    private void compile() throws HtSemanticException {

        try {
            String scriptText = partModel.getKnownProperty(ButtonModel.PROP_SCRIPT).toString();
            script = Interpreter.compile(scriptText);
        } catch (Exception e) {
            throw new HtSemanticException(e.getMessage());
        }
    }

    @Override
    public void sendMessage(String message) {
        Interpreter.executeHandler(new PartIdSpecifier(PartType.BUTTON, getId()), script, message);
    }

    @Override
    public Value executeUserFunction(String function, ExpressionList arguments) throws HtSemanticException {
        return Interpreter.executeFunction(new PartIdSpecifier(PartType.BUTTON, getId()), script.getFunction(function), arguments);
    }

    @Override
    public boolean isPartToolActive() {
        return ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (ToolsContext.getInstance().getToolMode() == ToolMode.BROWSE && SwingUtilities.isLeftMouseButton(e)) {
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
            case ButtonModel.PROP_TOP:
            case ButtonModel.PROP_LEFT:
            case ButtonModel.PROP_WIDTH:
            case ButtonModel.PROP_HEIGHT:
                getButtonComponent().setBounds(partModel.getRect());
                getButtonComponent().validate();
                getButtonComponent().repaint();
                break;
            case ButtonModel.PROP_ENABLED:
                getButtonComponent().setEnabled(newValue.booleanValue());
                break;
            case ButtonModel.PROP_VISIBLE:
                setVisibleOnCard(newValue.booleanValue());
                break;
            case AbstractPartModel.PROP_ZORDER:
                getParentCard().onZOrderChanged();
                break;
        }
    }
}
