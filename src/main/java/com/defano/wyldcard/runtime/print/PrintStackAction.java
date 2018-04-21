package com.defano.wyldcard.runtime.print;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.window.WindowManager;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

public class PrintStackAction extends PrintActionDelegate {

    private int currentCard;

    @Override
    public void onPrintRequested() {
        this.jobName = "Stack " + WindowManager.getInstance().getFocusedStack().getStackModel().getStackName(new ExecutionContext());
    }

    @Override
    public void onPrintStarted() {
        this.currentCard = WyldCard.getInstance().getActiveStackDisplayedCard().getCardModel().getCardIndexInStack();
    }

    @Override
    protected void onPrintCompleted(boolean successfully) {
        WindowManager.getInstance().getFocusedStack().goCard(new ExecutionContext(), currentCard, null, false);
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex < WindowManager.getInstance().getFocusedStack().getCardCountProvider().blockingFirst()) {
            // Translate printable content to top-left printable coordinate of the page
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Need to transition to card in order to print it
            WindowManager.getInstance().getFocusedStack().goCard(new ExecutionContext(), pageIndex, null, false).printAll(g);

            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

}
