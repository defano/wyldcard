package com.defano.wyldcard.editor;

import com.defano.hypertalk.ast.model.Script;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.runtime.compiler.CompileCompletionObserver;
import com.defano.wyldcard.runtime.compiler.ScriptCompiler;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;

public class HyperTalkSyntaxParser extends AbstractParser implements CompileCompletionObserver {

    private final SyntaxParserDelegate delegate;
    private final DefaultParseResult previousParseResult = new DefaultParseResult(this);

    public HyperTalkSyntaxParser(SyntaxParserDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        try {
            delegate.onCompileStarted();

            String scriptText = doc.getText(0, doc.getLength());
            ScriptCompiler.asyncBestEffortCompile(delegate.getParseCompilationUnit(), scriptText, this);
        } catch (BadLocationException e) {
            // Impossible
        }

        // This represents the *last* result of parsing the script... the issue here is that RSyntaxTextArea foolishly
        // doesn't support parsing on a background thread. So, we work around this by running the parse on a worker
        // thread and synchronously returning the last result. If the worker thread detects that the error state has
        // changed, it forces a re-parse to assure the change is picked up immediately.

        return previousParseResult;
    }

    @Override
    public void onCompileCompleted(String scriptText, Object compiledScript, HtException generatedError) {
        SwingUtilities.invokeLater(() -> {
            boolean dirtiedResult;

            // Syntax error detected
            if (generatedError != null) {
                String errorMessage = generatedError.getMessage();
                dirtiedResult = !hasError(errorMessage);

                previousParseResult.clearNotices();
                previousParseResult.addNotice(new DefaultParserNotice(
                        HyperTalkSyntaxParser.this,
                        errorMessage,
                        generatedError.getBreadcrumb().getToken().getLine() - 1
                ));

                delegate.onCompileCompleted(null, errorMessage);
            }

            // Script contains no syntax errors
            else {
                dirtiedResult = !previousParseResult.getNotices().isEmpty();
                previousParseResult.clearNotices();

                delegate.onCompileCompleted((Script) compiledScript, null);
            }

            // If previousParseResult was changed, tell RSyntaxTextArea to ask again
            if (dirtiedResult) {
                delegate.onRequestParse(this);
            }

        });
    }

    private boolean hasError(String error) {
        for (ParserNotice thisNotice : previousParseResult.getNotices()) {
            if (thisNotice.getMessage().equals(error)) {
                return true;
            }
        }

        return false;
    }
}
