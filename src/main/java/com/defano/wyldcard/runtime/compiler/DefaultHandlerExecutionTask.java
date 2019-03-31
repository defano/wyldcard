package com.defano.wyldcard.runtime.compiler;

import com.defano.hypertalk.ast.preemptions.PassPreemption;
import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.preemptions.TerminateHandlerPreemption;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.debug.message.HandlerInvocation;
import com.defano.wyldcard.debug.message.HandlerInvocationBridge;
import com.defano.wyldcard.runtime.context.ExecutionContext;

import java.util.List;

public class DefaultHandlerExecutionTask implements HandlerExecutionTask {

    private final ExecutionContext context;
    private final NamedBlock handler;
    private final PartSpecifier me;
    private final ListExp arguments;

    public DefaultHandlerExecutionTask(ExecutionContext context, PartSpecifier me, NamedBlock handler, ListExp arguments) {
        this.context = context;
        this.handler = handler;
        this.me = me;
        this.arguments = arguments;
    }

    @Override
    public Boolean call() throws HtException {
        boolean trapped = true;

        // Arguments passed to handler must be evaluated in the context of the caller (i.e., before we push a new stack frame)
        List<Value> evaluatedArguments = arguments.evaluateAsList(context);

        HandlerInvocationBridge.getInstance().notifyMessageHandled(new HandlerInvocation(Thread.currentThread().getName(), handler.name, evaluatedArguments, me, context.getTarget() == null, context.getStackDepth(), true));

        // Push a new context
        context.pushStackFrame(handler.getLineNumber(), handler.name, me, evaluatedArguments);

        // Target refers to the part first receiving the message
        if (context.getTarget() == null) {
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
        }

        // Script invoked 'pass {handlerName}' command; check semantics
        catch (PassPreemption e) {
            trapped = false;
        }

        // Script invoked 'exit {handlerName}' command; check semantics
        catch (TerminateHandlerPreemption e) {
            // Nothing to do
        }

        // Script invoked some other (illegal) form of exit (like 'exit repeat')
        catch (Preemption e) {
            WyldCard.getInstance().showErrorDialogAndAbort(new HtSemanticException("Cannot exit from here."));
        }

        // Pop context
        context.popStackFrame();

        return trapped;
    }
}
