package com.defano.wyldcard.importer.result;

import java.util.ArrayList;

public class Results {

    private final ArrayList<Issue> warnings = new ArrayList<>();
    private final ArrayList<Issue> errors = new ArrayList<>();

    public void error(Issue issue) {
        errors.add(issue);
    }

    public void warn(Issue issue) {
        warnings.add(issue);
    }

    public boolean isSuccessful() {
        return errors.isEmpty();
    }
}
