package com.defano.hypercard.runtime;

import com.defano.hypertalk.ast.common.Script;
import com.defano.hypertalk.exception.HtException;

public interface CompileCompletionObserver {
    void onCompileCompleted(String scriptText, Script compiledScript, HtException generatedError);
}
