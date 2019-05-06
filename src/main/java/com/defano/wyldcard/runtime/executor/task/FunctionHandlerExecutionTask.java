package com.defano.wyldcard.runtime.executor.task;

import com.defano.hypertalk.ast.ASTNode;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.preemptions.*;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.debug.message.HandlerInvocation;
import com.defano.wyldcard.debug.message.HandlerInvocationCache;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;
import java.util.concurrent.Callable;

public class FunctionHandlerExecutionTask implements Callable<Value> {

    private final ExecutionContext context;
    private final ASTNode callingNode;
    private final NamedBlock function;
    private final List<Value> evaluatedArguments;
    private final PartSpecifier me;

    public FunctionHandlerExecutionTask(ExecutionContext context, ASTNode callingNode, PartSpecifier me, NamedBlock function, List<Value> arguments) {
        this.context = context;
        this.callingNode = callingNode;
        this.function = function;
        this.evaluatedArguments = arguments;
        this.me = me;
    }

    @Override
    public Value call() throws HtException {

        HandlerInvocationCache.getInstance().notifyMessageHandled(new HandlerInvocation(Thread.currentThread().getName(), function.name, evaluatedArguments, me, true, context.getStackDepth(), true));

        context.pushStackFrame(callingNode, function.name, me, evaluatedArguments);

        // Bind argument values to parameter variables in this context
        for (int index = 0; index < function.parameters.list.size(); index++) {

            // Missing arguments are populated with empty
            String theParam = function.parameters.list.get(index);
            Value theArg = evaluatedArguments.size() > index ? evaluatedArguments.get(index) : new Value();

            context.setVariable(theParam, theArg);
        }

        try {
            function.statements.execute(context);
        } catch (TerminateIterationPreemption p) {
            throw new HtSemanticException("Can't continue from here.");
        } catch (TerminateLoopPreemption p) {
            throw new HtSemanticException("Can't exit from here.");
        } catch (Preemption p) {
            // Nothing to do
        }

        Value returnValue = context.getStackFrame().getReturnValue();
        context.popStackFrame();
        return returnValue;
    }
}
