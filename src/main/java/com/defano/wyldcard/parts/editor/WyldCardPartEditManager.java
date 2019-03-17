package com.defano.wyldcard.parts.editor;

import com.defano.hypertalk.ast.model.Value;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.paint.ToolMode;
import com.defano.wyldcard.parts.ToolEditablePart;
import com.defano.wyldcard.parts.button.ButtonModel;
import com.defano.wyldcard.parts.button.ButtonPart;
import com.defano.wyldcard.parts.card.CardPart;
import com.defano.wyldcard.parts.field.FieldPart;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.inject.Singleton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

@Singleton
public class WyldCardPartEditManager implements PartEditManager {

    // Initial size of new part when user command-drags
    private final static Dimension NEW_PART_DIM = new Dimension(10,10);
    private Point clickLoc;
    private boolean isScriptEditMode = false;

    @Override
    public void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        ToolEditablePart part = WyldCard.getInstance().getPartToolManager().getSelectedPart();

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
            if (WyldCard.getInstance().getKeyboardManager().isCtrlCommandDown() && part == null){
                if (WyldCard.getInstance().getToolsManager().getToolMode() == ToolMode.BUTTON) {
                    doNewButton();
                } else if (WyldCard.getInstance().getToolsManager().getToolMode() == ToolMode.FIELD) {
                    doNewField();
                }
            }
        }
    }

    private void doNewField() {
        CardPart theCard = WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getDisplayedCard();
        FieldPart theField = theCard.newField(new ExecutionContext(), new Rectangle(clickLoc, NEW_PART_DIM));
        WyldCard.getInstance().getPartToolManager().setSelectedPart(theField);

        new PartResizer(theField, theCard);
    }

    private void doNewButton() {
        CardPart theCard = WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getDisplayedCard();
        ButtonPart theButton = theCard.newButton(new ExecutionContext(), new Rectangle(clickLoc, NEW_PART_DIM));
        theButton.getPartModel().setKnownProperty(new ExecutionContext(), ButtonModel.PROP_STYLE, new Value("transparent"));
        theButton.getPartModel().setKnownProperty(new ExecutionContext(), ButtonModel.PROP_SHOWNAME, new Value("false"));

        WyldCard.getInstance().getPartToolManager().setSelectedPart(theButton);

        new PartResizer(theButton, theCard);
    }

    private void doPartEdit(ToolEditablePart part) {
        CardPart theCard = WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getDisplayedCard();
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
            WyldCard.getInstance().getPartToolManager().deselectAllParts();
        }
    }

    private void updateClickLoc() {
        CardPart theCard = WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getDisplayedCard();
        this.clickLoc = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(clickLoc, theCard);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        boolean isAltOptionDown = e.isAltDown();
        boolean isCtrlCommandDown = e.isMetaDown() || e.isControlDown();
        CardPart theCard = WyldCard.getInstance().getWindowManager().getFocusedStackWindow().getDisplayedCard();

        if (isScriptEditMode) {
            theCard.repaint();
        }

        isScriptEditMode = isAltOptionDown && isCtrlCommandDown;

        if (isScriptEditMode) {
            theCard.repaint();
        }

        return false;
    }
}
