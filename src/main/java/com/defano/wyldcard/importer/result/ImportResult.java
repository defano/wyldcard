package com.defano.wyldcard.importer.result;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.ImportException;
import com.defano.wyldcard.importer.block.Block;

import java.util.ArrayList;

public class ImportResult {

    private final ArrayList<Issue> warnings = new ArrayList<>();
    private final ArrayList<Issue> errors = new ArrayList<>();

    public void error(Block source, String error) throws ImportException {
        error(source, error, null);
    }

    public void error(Block source, String error, Exception cause) throws ImportException {
        errors.add(new Issue(error, source, cause));
        throw new ImportException(this);
    }

    public void warn(Block source, String error) {
        warn(source, error, null);
    }

    public void warn(Block source, String error, Exception cause) {
        warnings.add(new Issue(error, source, cause));
    }

    public boolean isSuccessful() {
        return errors.isEmpty();
    }

    public ArrayList<Issue> getWarnings() {
        return warnings;
    }

    public ArrayList<Issue> getErrors() {
        return errors;
    }

    private class Issue {
        private final String message;
        private final Block source;
        private final Exception cause;

        public Issue(String message, Block source, Exception cause) {
            this.message = message;
            this.source = source;
            this.cause = cause;
        }
    }
}
