package com.defano.wyldcard.editor;

import com.defano.hypertalk.ast.model.Script;
import com.defano.wyldcard.aspect.RunOnDispatch;
import com.defano.wyldcard.runtime.compiler.CompilationUnit;
import org.fife.ui.rsyntaxtextarea.parser.Parser;

public interface SyntaxParserDelegate {

    default CompilationUnit getParseCompilationUnit() {
        return CompilationUnit.SCRIPT;
    }

    @RunOnDispatch
    void onRequestParse(Parser syntaxParser);

    @RunOnDispatch
    void onCompileStarted();

    @RunOnDispatch
    void onCompileCompleted(Script compiledScript, String resultMessage);
}
