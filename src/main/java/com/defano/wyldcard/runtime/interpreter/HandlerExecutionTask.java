package com.defano.wyldcard.runtime.interpreter;

import com.defano.hypertalk.ast.breakpoints.Preemption;
import com.defano.hypertalk.ast.breakpoints.TerminateHandlerPreemption;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.debug.watch.message.HandlerInvocation;
import com.defano.wyldcard.debug.watch.message.HandlerInvocationBridge;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;
import java.util.concurrent.Callable;

public class HandlerExecutionTask implements Callable<String> {

    private final ExecutionContext context;
    private final NamedBlock handler;
    private final PartSpecifier me;
    private final ListExp arguments;
    private final boolean isTheTarget;

    public HandlerExecutionTask(ExecutionContext context, PartSpecifier me, boolean isTheTarget, NamedBlock handler, ListExp arguments) {
        this.context = context;
        this.handler = handler;
        this.me = me;
        this.arguments = arguments;
        this.isTheTarget = isTheTarget;
    }

    @Override
    public String call() throws HtException {

        // Arguments passed to handler must be evaluated in the context of the caller (i.e., before we push a new stack frame)
        List<Value> evaluatedArguments = arguments.evaluateAsList(context);

        HandlerInvocationBridge.getInstance().notifyMessageHandled(new HandlerInvocation(Thread.currentThread().getName(), handler.name, evaluatedArguments, me, context.getStackDepth(), !handler.isEmptyPassBlock()));

        // Push a new context
        context.pushStackFrame();
        context.pushMe(me);
        context.setMessage(handler.name);
        context.setParams(evaluatedArguments);

        if (isTheTarget) {
            context.setTarget(me);
        }

        // Bind argument values to parameter variables in this context
        for (int index = 0; index < handler.parameters.list.size(); index++) {
            String theParam = handler.parameters.list.get(index);

            // Handlers may be invoked with missing arguments; assume empty for missing args
            Value theArg = index >= evaluatedArguments.size() ? new Value() : evaluatedArguments.get(index);
            context.setVariable(theParam, theArg);
        }

        // Execute handler
        try {
            handler.statements.execute(context);
        } catch (TerminateHandlerPreemption e) {
            if (e.getHandlerName() != null && !e.getHandlerName().equalsIgnoreCase(handler.name)) {
                WyldCard.getInstance().showErrorDialog(new HtSemanticException("Cannot exit '" + e.getHandlerName() + "' from inside '" + handler.name + "'."));
            }
        } catch (Preemption e) {
            WyldCard.getInstance().showErrorDialog(new HtSemanticException("Cannot exit from here."));
        }

        String passedMessage = context.getPassedMessage();

        // Pop context
        context.popStackFrame();
        context.popMe();

        return passedMessage;
    }
}
