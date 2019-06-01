package com.defano.wyldcard.editor;

import com.defano.wyldcard.editor.help.SyntaxHelpModel;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HyperTalkCompletionProvider extends DefaultCompletionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(HyperTalkCompletionProvider.class);
    private final List<Completion> completionList = new ArrayList<>();

    public HyperTalkCompletionProvider() {

        for (CompletionLibrary category : CompletionLibrary.values()) {
            unpack(category);
        }

        addCompletions(completionList);
    }

    private void unpack(CompletionLibrary category) {
        try {
            Collection<SyntaxHelpModel> models = SyntaxHelpModel.fromJson(category.getJson());

            for (SyntaxHelpModel thisModel : models) {
                AutoCompletionBuilder
                        .fromSyntaxHelpModel(thisModel, category.getName(), category.getIcon())
                        .buildInto(completionList, this);
            }

        } catch (IOException e) {
            LOG.error("An error occurred unpacking the completion library. Malformed syntax?", e);
        }
    }
}