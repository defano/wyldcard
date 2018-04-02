package com.defano.wyldcard.editor;

import com.defano.wyldcard.editor.help.SyntaxHelpModel;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HyperTalkCompletionProvider extends DefaultCompletionProvider {

    private final List<Completion> completionList = new ArrayList<>();

    public HyperTalkCompletionProvider() {

        unpack("syntax-help/constructs.json");
        unpack("syntax-help/commands.json");

        addCompletions(completionList);
    }

    private void unpack(String jsonResource) {
        try {
            Collection<SyntaxHelpModel> models = SyntaxHelpModel.fromJson(jsonResource);

            for (SyntaxHelpModel thisModel : models) {
                AutoCompletionBuilder.fromSyntaxHelpModel(thisModel).buildInto(completionList, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}