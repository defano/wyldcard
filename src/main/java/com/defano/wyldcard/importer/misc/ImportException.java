package com.defano.wyldcard.importer.misc;

import com.defano.wyldcard.importer.misc.ImportResult;

public class ImportException extends Exception {

    private final ImportResult report;

    public ImportException(ImportResult report) {
        this.report = report;
    }

    public ImportResult getReport() {
        return report;
    }
}
