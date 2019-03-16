package com.defano.wyldcard.importer.misc;

import com.defano.wyldcard.importer.HyperCardStack;
import com.defano.wyldcard.importer.block.Block;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;

public class ImportResult {

    private final ArrayList<Issue> warnings = new ArrayList<>();
    private final ArrayList<Issue> errors = new ArrayList<>();

    public void throwError(Block source, String error) throws ImportException {
        throwError(source, error, null);
    }

    public void throwError(Block source, String error, Exception cause) throws ImportException {
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
        private final HyperCardStack stack;
        private final String message;
        private final Block source;
        private final Exception cause;

        public Issue(String message, Block source, Exception cause) {
            this.message = message;
            this.source = source;
            this.cause = cause;
            this.stack = source.getStack();
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
