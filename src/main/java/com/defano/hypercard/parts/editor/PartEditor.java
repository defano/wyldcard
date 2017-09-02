package com.defano.hypercard.parts.editor;

import com.defano.hypercard.context.PartToolContext;
import com.defano.hypercard.parts.ToolEditablePart;
import com.defano.hypercard.parts.card.CardPart;
import com.defano.hypercard.runtime.WindowManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

public class PartEditor implements AWTEventListener {

    public static void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new PartEditor(), AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    public void eventDispatched(AWTEvent event) {

        // User pressed the mouse
        if (event.getID() == MouseEvent.MOUSE_PRESSED) {

            // Is a part currently selected on the card?
            ToolEditablePart part = PartToolContext.getInstance().getSelectedPartProvider().get();
            if (part != null) {

                CardPart theCard = WindowManager.getStackWindow().getDisplayedCard();
                Point cardLocalMouseLoc = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(cardLocalMouseLoc, theCard);
                Point partLocalMouseLoc = SwingUtilities.convertPoint(theCard, cardLocalMouseLoc, part.getComponent());

                Rectangle r = new Rectangle();
                part.getComponent().getBounds(r);

                // User clicked resize drag handle
                if (part.getResizeDragHandle().contains(partLocalMouseLoc)) {
                    new PartResizer(part, theCard);
                }

                // User click inside of part
                else if (r.contains(cardLocalMouseLoc)) {
                    new PartMover(part, theCard).startMoving();
                }

                // User clicked away from part
                else {
                    PartToolContext.getInstance().deselectAllParts();
                }
            }
        }
        
    }
}
