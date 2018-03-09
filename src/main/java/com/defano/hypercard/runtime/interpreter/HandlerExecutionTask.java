package com.defano.hypercard.runtime.interpreter;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.breakpoints.TerminateHandlerBreakpoint;
import com.defano.hypertalk.ast.expressions.ListExp;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;
import java.util.concurrent.Callable;

public class HandlerExecutionTask implements Callable<String> {

    private final NamedBlock handler;
    private final PartSpecifier me;
    private final ListExp arguments;
    private final boolean isTheTarget;

    public HandlerExecutionTask(PartSpecifier me, boolean isTheTarget, NamedBlock handler, ListExp arguments) {
        this.handler = handler;
        this.me = me;
        this.arguments = arguments;
        this.isTheTarget = isTheTarget;
    }

    @Override
    public String call() throws HtException {

        // Arguments passed to handler must be evaluated in the context of the caller (i.e., before we push a new stack frame)
        List<Value> evaluatedArguments = arguments.evaluateAsList();

        // Push a new context
        ExecutionContext.getContext().pushContext();
        ExecutionContext.getContext().pushMe(me);
        ExecutionContext.getContext().setMessage(handler.name);
        ExecutionContext.getContext().setParams(evaluatedArguments);

        if (isTheTarget) {
            ExecutionContext.getContext().setTarget(me);
        }

        // Bind argument values to parameter variables in this context
        for (int index = 0; index < handler.parameters.list.size(); index++) {
            String theParam = handler.parameters.list.get(index);

            // Handlers may be invoked with missing arguments; assume empty for missing args
            Value theArg = index >= evaluatedArguments.size() ? new Value() : evaluatedArguments.get(index);
            ExecutionContext.getContext().setVariable(theParam, theArg);
        }

        // Execute handler
        try {
            handler.statements.execute();
        } catch (TerminateHandlerBreakpoint e) {
            if (e.getHandlerName() != null && !e.getHandlerName().equalsIgnoreCase(handler.name)) {
                HyperCard.getInstance().showErrorDialog(new HtSemanticException("Cannot exit '" + e.getHandlerName() + "' from inside '" + handler.name + "'."));
            }
        } catch (Breakpoint e) {
            HyperCard.getInstance().showErrorDialog(new HtSemanticException("Cannot exit from here."));
        }

        String passedMessage = ExecutionContext.getContext().getPassedMessage();

        // Pop context
        ExecutionContext.getContext().popContext();
        ExecutionContext.getContext().popMe();

        return passedMessage;
    }
}
