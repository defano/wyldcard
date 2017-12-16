package com.defano.hypercard.runtime;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.breakpoints.TerminateHandlerBreakpoint;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.ast.common.NamedBlock;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;
import java.util.concurrent.Callable;

public class HandlerExecutionTask implements Callable<String> {

    private final NamedBlock handler;
    private final PartSpecifier me;
    private final ExpressionList arguments;

    public HandlerExecutionTask(PartSpecifier me, NamedBlock handler, ExpressionList arguments) {
        this.handler = handler;
        this.me = me;
        this.arguments = arguments;
    }

    @Override
    public String call() throws HtException {

        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        // Arguments passed to function must be evaluated in the context of the caller (i.e., before we push a new stack frame)
        List<Value> evaluatedArguments = arguments.evaluateDisallowingCoordinates();

        ExecutionContext.getContext().pushContext();
        ExecutionContext.getContext().pushMe(me);
        ExecutionContext.getContext().setParams(evaluatedArguments);
        ExecutionContext.getContext().setMessage(handler.name);

        // Bind argument values to parameter variables in this context
        for (int index = 0; index < handler.parameters.list.size(); index++) {
            String theParam = handler.parameters.list.get(index);

            // Handlers may be invoked with missing arguments; assume empty for missing args
            Value theArg = index >= evaluatedArguments.size() ? new Value() : evaluatedArguments.get(index);
            ExecutionContext.getContext().setVariable(theParam, theArg);
        }

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

        ExecutionContext.getContext().popContext();
        ExecutionContext.getContext().popMe();

        return passedMessage;
    }
}
