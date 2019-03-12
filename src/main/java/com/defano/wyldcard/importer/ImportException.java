package com.defano.wyldcard.importer;

import com.defano.wyldcard.importer.result.ImportResult;

public class ImportException extends Exception {

    private final ImportResult report;

    public ImportException(ImportResult report) {
        this.report = report;
    }

    public ImportResult getReport() {
        return report;
    }
}
