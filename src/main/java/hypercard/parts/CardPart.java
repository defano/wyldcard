/**
 * CardPart.java
 *
 * @author matt.defano@motorola.com
 * <p>
 * Implements a card part by extending the Swing panel object.
 */

package hypercard.parts;

import com.defano.jmonet.canvas.ChangeSet;
import com.defano.jmonet.canvas.PaintCanvas;
import com.defano.jmonet.canvas.UndoablePaintCanvas;
import com.defano.jmonet.canvas.observable.CanvasCommitObserver;
import hypercard.HyperCard;
import hypercard.context.PartToolContext;
import hypercard.context.PartsTable;
import hypercard.context.ToolsContext;
import hypercard.gui.util.FileDrop;
import hypercard.gui.util.ImageImporter;
import hypercard.parts.model.*;
import hypercard.parts.model.ButtonModel;
import hypertalk.ast.common.PartType;
import hypertalk.ast.containers.PartSpecifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class CardPart extends JLayeredPane implements CanvasCommitObserver {

    private final static int BACKGROUND_CANVAS_LAYER = 0;
    private final static int BACKGROUND_PARTS_LAYER = 1;
    private final static int FOREGROUND_CANVAS_LAYER = 2;
    private final static int FOREGROUND_PARTS_LAYER = 3;

    private CardModel model;

    private PartsTable<FieldPart> fields = new PartsTable<>();
    private PartsTable<ButtonPart> buttons = new PartsTable<>();

    private UndoablePaintCanvas foregroundCanvas;
    private UndoablePaintCanvas backgroundCanvas;

    private transient StackModel stack;

    private CardPart() {
        super();

        this.setLayout(null);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
                stack.fireOnCardOpened(CardPart.this);
            }
        });
    }

    public static CardPart fromModel(CardModel model, StackModel stack) throws Exception {
        CardPart card = new CardPart();
        card.model = model;
        card.stack = stack;

        for (AbstractPartModel thisPart : model.getPartModels()) {
            switch (thisPart.getType()) {
                case BUTTON:
                    ButtonPart button = ButtonPart.fromModel(card, (ButtonModel) thisPart);
                    card.buttons.addPart(button);
                    card.addSwingComponent(button.getComponent(), button.getRect());
                    break;
                case FIELD:
                    FieldPart field = FieldPart.fromModel(card, (FieldModel) thisPart);
                    card.fields.addPart(field);
                    card.addSwingComponent(field.getComponent(), field.getRect());
                default:
            }
        }

        card.foregroundCanvas = new UndoablePaintCanvas(model.getCardImage());
        card.foregroundCanvas.addCanvasCommitObserver(card);
        card.backgroundCanvas = new UndoablePaintCanvas(card.getCardBackground().getBackgroundImage());
        card.backgroundCanvas.addCanvasCommitObserver(card);

        card.foregroundCanvas.setSize(stack.getWidth(), stack.getHeight());
        card.backgroundCanvas.setSize(stack.getWidth(), stack.getHeight());

        card.setLayer(card.foregroundCanvas, FOREGROUND_CANVAS_LAYER);
        card.add(card.foregroundCanvas);

        card.setLayer(card.backgroundCanvas, BACKGROUND_CANVAS_LAYER);
        card.add(card.backgroundCanvas);

        card.setMaximumSize(new Dimension(stack.getWidth(), stack.getHeight()));
        card.setSize(stack.getWidth(), stack.getHeight());

        card.foregroundCanvas.getScaleProvider().addObserver((o, arg) -> card.setBackgroundVisible(((double) arg) == 1.0));
        card.backgroundCanvas.getScaleProvider().addObserver((o, arg) -> card.setForegroundVisible(((double) arg) == 1.0));

        ToolsContext.getInstance().isEditingBackgroundProvider().addObserver((oldValue, isEditingBackground) -> card.setForegroundVisible(!(boolean) isEditingBackground));

        for (ButtonPart thisButton : card.buttons.getParts()) {
            thisButton.getPartModel().notifyPropertyChangedObserver(thisButton);
        }

        for (FieldPart thisField : card.fields.getParts()) {
            thisField.getPartModel().notifyPropertyChangedObserver(thisField);
        }

        // Import image files that are dropped onto the card
        new FileDrop(card, files -> ImageImporter.importAsSelection(files[0]));

        return card;
    }

    public void addField(FieldPart field) throws PartException {
        model.addPart(field);
        fields.addPart(field);
        addSwingComponent(field.getComponent(), field.getRect());
        field.partOpened();
    }

    public void removeField(FieldPart field) {
        model.removePart(field);
        fields.removePart(field);
        removeSwingComponent(field.getComponent());
        field.partClosed();
    }

    public void addButton(ButtonPart button) throws PartException {
        model.addPart(button);
        buttons.addPart(button);
        addSwingComponent(button.getComponent(), button.getRect());
        button.partOpened();
    }

    public void removeButton(ButtonPart button) {
        model.removePart(button);
        buttons.removePart(button);
        removeSwingComponent(button.getComponent());
        button.partClosed();
    }

    public void newButton() {
        try {
            ButtonPart newButton = ButtonPart.newButton(this);
            addButton(newButton);
            PartToolContext.getInstance().setSelectedPart(newButton);
        } catch (PartException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void newField() {
        try {
            FieldPart newField = FieldPart.newField(this);
            addField(newField);
            PartToolContext.getInstance().setSelectedPart(newField);
        } catch (PartException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Part getPart(PartSpecifier ps) throws PartException {
        if (ps.type() == PartType.FIELD)
            return fields.getPart(ps);
        else if (ps.type() == PartType.BUTTON)
            return buttons.getPart(ps);
        else
            throw new RuntimeException("Unhandled part type");
    }

    public Collection<ButtonPart> getButtons() {
        return buttons.getParts();
    }

    public Collection<FieldPart> getFields() {
        return fields.getParts();
    }

    public List<Part> getPartsInZOrder() {
        ArrayList<Part> joined = new ArrayList<>();
        joined.addAll(getButtons());
        joined.addAll(getFields());

        Comparator<Part> zOrderComparator = (o1, o2) -> new Integer(o1.getPartModel().getKnownProperty(AbstractPartModel.PROP_ZORDER).integerValue())
                .compareTo(o2.getPartModel().getKnownProperty(AbstractPartModel.PROP_ZORDER).integerValue());

        joined.sort(zOrderComparator);

        return joined;
    }

    public UndoablePaintCanvas getCanvas() {
        return ToolsContext.getInstance().isEditingBackground() ? backgroundCanvas : foregroundCanvas;
    }

    public void setForegroundVisible(boolean isVisible) {
        foregroundCanvas.setVisible(isVisible);

        for (Component thisComponent : getComponentsInLayer(FOREGROUND_PARTS_LAYER)) {
            thisComponent.setVisible(isVisible);
        }
    }

    public void setBackgroundVisible(boolean isVisible) {
        backgroundCanvas.setVisible(isVisible);
    }

    public BackgroundModel getCardBackground() {
        return stack.getBackground(model.getBackgroundId());
    }

    public void invalidateSwingComponent(Part forPart, Component oldButtonComponent, Component newButtonComponent) {
        removeSwingComponent(oldButtonComponent);
        addSwingComponent(newButtonComponent, forPart.getRect());
    }

    private void removeSwingComponent(Component component) {
        remove(component);
        revalidate();
        repaint();
    }

    private void addSwingComponent(Component component, Rectangle bounds) {
        component.setBounds(bounds);
        setLayer(component, FOREGROUND_PARTS_LAYER);
        add(component);
        moveToFront(component);
        revalidate();
        repaint();
    }


    public int nextFieldId() {
        return fields.getNextId();
    }

    public int nextButtonId() {
        return buttons.getNextId();
    }

    public void onZOrderChanged() {
        SwingUtilities.invokeLater(() -> {
            for (Part thisPart : getPartsInZOrder()) {
                moveToBack(thisPart.getComponent());
            }
        });
    }

    @Override
    public void onCommit(PaintCanvas canvas, ChangeSet changeSet, BufferedImage canvasImage) {
        if (ToolsContext.getInstance().isEditingBackground()) {
            getCardBackground().setBackgroundImage(canvasImage);
        } else {
            model.setCardImage(canvasImage);
        }
    }
}
