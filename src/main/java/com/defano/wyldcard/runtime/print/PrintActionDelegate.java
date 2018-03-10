package com.defano.wyldcard.runtime.print;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public abstract class PrintActionDelegate implements Printable, ActionListener {

    protected String jobName;

    protected void onPrintRequested() {
        // Nothing to do; for override in subclasses
    }

    protected void onPrintStarted() {
        // Nothing to do; for override in subclasses
    }

    protected void onPrintCompleted(boolean successfully) {
        // Nothing to do; for override in subclasses
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        onPrintRequested();

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        if (jobName != null) {
            job.setJobName(jobName);
        }

        if (job.printDialog()) {
            try {
                onPrintStarted();
                job.print();
                onPrintCompleted(true);
            } catch (PrinterException ex) {
                onPrintCompleted(false);
            }
        }
    }
}
