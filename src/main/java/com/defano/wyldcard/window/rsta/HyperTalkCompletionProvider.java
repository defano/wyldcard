package com.defano.wyldcard.window.rsta;

import com.defano.hypertalk.parser.HyperTalkLexer;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import java.util.ArrayList;
import java.util.List;

public class HyperTalkCompletionProvider extends DefaultCompletionProvider {

    public HyperTalkCompletionProvider() {
        List<Completion> completionList = new ArrayList<>();

        for (int index = 0; index < HyperTalkLexer.VOCABULARY.getMaxTokenType(); index++) {
            String lexeme = HyperTalkLexer.VOCABULARY.getLiteralName(index);
            if (lexeme != null && lexeme.length() > 3) {
                lexeme = lexeme.substring(1, lexeme.length() - 1);
                completionList.add(new BasicCompletion(this, lexeme));
            }
        }

        addCompletions(completionList);
    }

}
