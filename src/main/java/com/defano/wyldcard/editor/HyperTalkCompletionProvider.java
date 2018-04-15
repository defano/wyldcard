package com.defano.wyldcard.editor;

import com.defano.wyldcard.editor.help.SyntaxHelpModel;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HyperTalkCompletionProvider extends DefaultCompletionProvider {

    private final List<Completion> completionList = new ArrayList<>();
    private final CompletionCellRenderer completionCellRenderer = new CompletionCellRenderer();

    public HyperTalkCompletionProvider() {

        for (CompletionLibrary category : CompletionLibrary.values()) {
            unpack(category);
        }

        completionCellRenderer.setShowTypes(true);
        setListCellRenderer(completionCellRenderer);

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
            e.printStackTrace();
        }
    }
}