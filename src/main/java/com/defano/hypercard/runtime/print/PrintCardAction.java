package com.defano.hypercard.runtime.print;

import com.defano.hypercard.HyperCard;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

public class PrintCardAction extends PrintActionDelegate {

    @Override
    public void onPrintRequested() {
        StringBuilder jobNameBuilder = new StringBuilder();

        jobNameBuilder.append("Card ");
        jobNameBuilder.append(HyperCard.getInstance().getCard().getCardIndexInStack() + 1);
        jobNameBuilder.append(" in ");
        jobNameBuilder.append(HyperCard.getInstance().getStack().getStackModel().getStackName());

        super.jobName = jobNameBuilder.toString();
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        // Translate printable content to top-left printable coordinate of the page
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        HyperCard.getInstance().getCard().printAll(g);

        return PAGE_EXISTS;
    }

}
