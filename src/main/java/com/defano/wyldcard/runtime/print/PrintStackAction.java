package com.defano.wyldcard.runtime.print;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

public class PrintStackAction extends PrintActionDelegate {

    private int currentCard;

    @Override
    public void onPrintRequested() {
        this.jobName = "Stack " + WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel().getStackName(new ExecutionContext());
    }

    @Override
    public void onPrintStarted() {
        this.currentCard = WyldCard.getInstance().getStackManager().getFocusedCard().getCardModel().getCardIndexInStack();
    }

    @Override
    protected void onPrintCompleted(boolean successfully) {
        WyldCard.getInstance().getStackManager().getFocusedStack().gotoCard(new ExecutionContext(), currentCard, null, false);
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex < WyldCard.getInstance().getStackManager().getFocusedStack().getCardCountProvider().blockingFirst()) {
            // Translate printable content to top-left printable coordinate of the page
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Need to transition to card in order to print it
            WyldCard.getInstance().getStackManager().getFocusedStack().gotoCard(new ExecutionContext(), pageIndex, null, false).printAll(g);

            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

}
