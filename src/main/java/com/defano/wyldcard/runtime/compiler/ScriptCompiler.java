package com.defano.wyldcard.runtime.compiler;

import com.defano.hypertalk.exception.HtException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ScriptCompiler {

    private static final int MAX_COMPILE_THREADS = 6;          // Simultaneous background parse tasks
    private static final ThreadPoolExecutor bestEffortCompileExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_COMPILE_THREADS, new ThreadFactoryBuilder().setNameFormat("be-async-compiler-%d").build());

    private ScriptCompiler() {
    }

    /**
     * Attempts to compile the given script text on a background thread and invoke the CompileCompletionObserver
     * (on the background thread) when complete.
     * <p>
     * This method cancels any previously requested compilation tasks except those that may already be executing. Thus,
     * invocation of the completion observer is not guaranteed; some jobs will be canceled before they run and thus
     * never complete.
     * <p>
     * This method is primarily useful for parse-as-you-type syntax checking.
     *
     * @param compilationUnit The type of script/scriptlet to compile
     * @param scriptText      The script to parse.
     * @param observer        A non-null callback to fire when compilation is complete.
     */
    public static void asyncBestEffortCompile(CompilationUnit compilationUnit, String scriptText, CompileCompletionObserver observer) {

        // Preempt any previously enqueued parse jobs
        ScriptCompiler.bestEffortCompileExecutor.getQueue().clear();
        ScriptCompiler.bestEffortCompileExecutor.submit(ScriptCompiler.createCompileTask(compilationUnit, scriptText, observer));
    }

    /**
     * Compiles the given script on the current thread.
     *
     * @param compilationUnit The type of script/scriptlet to compile
     * @param scriptText      The script text to parse.
     * @return The compiled Script object (the root of the abstract syntax tree)
     * @throws HtException Thrown if an error (i.e., syntax error) occurs when compiling.
     */
    public static Object blockingCompile(CompilationUnit compilationUnit, String scriptText) throws HtException {
        return TwoPhaseParser.parseScript(compilationUnit, scriptText);
    }

    /**
     * Gets a {@link Runnable} that, when executed, compiles the given script and notifies a
     * {@link CompileCompletionObserver}.
     *
     * @param compilationUnit The type of script/scriptlet to compile
     * @param scriptText      The script to parse.
     * @param observer        A non-null callback to fire when compilation is complete.
     * @return A runnable that compiles the script
     */
    private static Runnable createCompileTask(CompilationUnit compilationUnit, String scriptText, CompileCompletionObserver observer) {
        return () -> {
            HtException generatedError = null;
            Object compiledScript = null;

            try {
                compiledScript = TwoPhaseParser.parseScript(compilationUnit, scriptText);
            } catch (HtException e) {
                generatedError = e;
            } catch (Exception t) {
                generatedError = new HtException("An unexpected error occurred: " + t.getMessage());
            }

            observer.onCompileCompleted(scriptText, compiledScript, generatedError);
        };
    }
}
