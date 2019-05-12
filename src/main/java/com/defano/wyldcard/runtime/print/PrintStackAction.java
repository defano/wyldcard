package com.defano.wyldcard.runtime.print;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.part.stack.StackPart;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.awt.*;
import java.awt.print.PageFormat;

public class PrintStackAction extends PrintActionDelegate {

    private int currentCard;

    @Override
    public void onPrintRequested() {
        this.jobName = "Stack " + WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel().getStackName(new ExecutionContext());
    }

    @Override
    public void onPrintStarted() {
        this.currentCard = WyldCard.getInstance().getStackManager().getFocusedCard().getPartModel().getCardIndexInStack();
    }

    @Override
    protected void onPrintCompleted(boolean successfully) {
        StackPart focusedStack = WyldCard.getInstance().getStackManager().getFocusedStack();
        WyldCard.getInstance().getNavigationManager().goCard(new ExecutionContext(), focusedStack, currentCard, false);
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        StackPart focusedStack = WyldCard.getInstance().getStackManager().getFocusedStack();

        if (pageIndex < WyldCard.getInstance().getStackManager().getFocusedStack().getCardCountProvider().blockingFirst()) {
            // Translate printable content to top-left printable coordinate of the page
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Need to transition to card in order to print it
            WyldCard.getInstance().getNavigationManager().goCard(new ExecutionContext(), focusedStack, pageIndex, false).printAll(g);

            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

}
