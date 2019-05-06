package com.defano.wyldcard.runtime.print;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.runtime.ExecutionContext;

import java.awt.*;
import java.awt.print.PageFormat;

public class PrintCardAction extends PrintActionDelegate {

    @Override
    public void onPrintRequested() {
        StringBuilder jobNameBuilder = new StringBuilder();

        jobNameBuilder.append("Card ");
        jobNameBuilder.append(WyldCard.getInstance().getStackManager().getFocusedCard().getPartModel().getCardIndexInStack() + 1);
        jobNameBuilder.append(" in ");
        jobNameBuilder.append(WyldCard.getInstance().getStackManager().getFocusedStack().getStackModel().getStackName(new ExecutionContext()));

        super.jobName = jobNameBuilder.toString();
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {

        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        // Translate printable content to top-left printable coordinate of the page
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        WyldCard.getInstance().getStackManager().getFocusedCard().printAll(g);

        return PAGE_EXISTS;
    }

}
