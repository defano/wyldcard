package com.defano.hypercard.runtime.print;

import com.defano.hypercard.HyperCard;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

public class PrintStackAction extends PrintActionDelegate {

    private int currentCard;

    @Override
    public void onPrintRequested() {
        this.jobName = "Stack " + HyperCard.getInstance().getStack().getStackModel().getStackName();
    }

    @Override
    public void onPrintStarted() {
        this.currentCard = HyperCard.getInstance().getDisplayedCard().getCardIndexInStack();
    }

    @Override
    protected void onPrintCompleted(boolean successfully) {
        HyperCard.getInstance().getStack().goCard(currentCard, null);
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex < HyperCard.getInstance().getStack().getCardCountProvider().get()) {
            // Translate printable content to top-left printable coordinate of the page
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Need to transition to card in order to print it
            HyperCard.getInstance().getStack().goCard(pageIndex, null).printAll(g);

            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

}
