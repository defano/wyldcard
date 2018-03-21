package com.defano.wyldcard.window.rsta;

import com.defano.hypertalk.ast.model.Script;
import com.defano.wyldcard.aspect.RunOnDispatch;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

public interface SyntaxParserObserver {
    @RunOnDispatch
    void onRequestParse(Parser syntaxParser);

    @RunOnDispatch
    void onCompileStarted();

    @RunOnDispatch
    void onCompileCompleted(Script compiledScript, String resultMessage);
}
