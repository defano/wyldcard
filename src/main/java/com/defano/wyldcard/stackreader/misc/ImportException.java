package com.defano.wyldcard.stackreader.misc;

public class ImportException extends Exception {

    private final ImportResult report;

    public ImportException(ImportResult report) {
        this.report = report;
    }

    public ImportResult getReport() {
        return report;
    }
}
