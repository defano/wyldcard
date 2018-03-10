package com.defano.wyldcard.runtime.interpreter;

import com.defano.hypertalk.exception.HtException;

public interface CompileCompletionObserver {

    /**
     * Invoked after a compile has completed.
     *
     * @param scriptText The text of the script that was compiled.
     * @param compiledScript The compiled Script object.
     * @param generatedError Any exception generated during the compile process, or null, if the compile succeeded.
     */
    void onCompileCompleted(String scriptText, Object compiledScript, HtException generatedError);
}
