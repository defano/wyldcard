package com.defano.hypercard.parts.editor;

import com.defano.hypercard.awt.KeyboardManager;
import com.defano.hypercard.paint.ToolMode;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.button.ButtonPart;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.parts.field.FieldPart;
import com.defano.hypercard.runtime.context.PartToolContext;
import com.defano.hypercard.runtime.context.ToolsContext;
import com.defano.hypercard.window.WindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

public class PartEditor implements AWTEventListener {

    // Initial size of new part when user command-drags
    private final static Dimension NEW_PART_DIM = new Dimension(10,10);
    private Point clickLoc;

    public static void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new PartEditor(), AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        ToolEditablePart part = PartToolContext.getInstance().getSelectedPart();

        // User pressed the mouse
        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            updateClickLoc();

            // Is a part currently selected on the card?
            if (part != null) {
                doPartEdit(part);
            }
        }

        // User dragged the mouse
        else if (event.getID() == MouseEvent.MOUSE_DRAGGED) {
            if (KeyboardManager.isCtrlCommandDown && part == null){
                if (ToolsContext.getInstance().getToolMode() == ToolMode.BUTTON) {
                    doNewButton();
                } else if (ToolsContext.getInstance().getToolMode() == ToolMode.FIELD) {
                    doNewField();
                }
            }
        }
    }

    private void doNewField() {
        CardPart theCard = WindowManager.getStackWindow().getDisplayedCard();
        FieldPart theField = theCard.newField(new Rectangle(clickLoc, NEW_PART_DIM));
        PartToolContext.getInstance().setSelectedPart(theField);

        new PartResizer(theField, theCard);
    }

    private void doNewButton() {
        CardPart theCard = WindowManager.getStackWindow().getDisplayedCard();
        ButtonPart theButton = theCard.newButton(new Rectangle(clickLoc, NEW_PART_DIM));
        PartToolContext.getInstance().setSelectedPart(theButton);

        new PartResizer(theButton, theCard);
    }

    private void doPartEdit(ToolEditablePart part) {
        CardPart theCard = WindowManager.getStackWindow().getDisplayedCard();
        Point partLocalMouseLoc = SwingUtilities.convertPoint(theCard, clickLoc, part.getComponent());

        Rectangle r = new Rectangle();
        part.getComponent().getBounds(r);

        // User clicked resize drag handle
        if (part.getResizeDragHandle().contains(partLocalMouseLoc)) {
            new PartResizer(part, theCard);
        }

        // User click inside of part
        else if (r.contains(clickLoc)) {
            new PartMover(part, theCard).startMoving();
        }

        // User clicked away from part (and not a menu item)
        else if (theCard.contains(clickLoc) &&
                MenuSelectionManager.defaultManager().getSelectedPath().length == 0)
        {
            PartToolContext.getInstance().deselectAllParts();
        }
    }

    private void updateClickLoc() {
        CardPart theCard = WindowManager.getStackWindow().getDisplayedCard();
        this.clickLoc = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(clickLoc, theCard);
    }

}
