package com.defano.wyldcard.runtime.interpreter;

import com.defano.wyldcard.WyldCard;
import com.defano.wyldcard.debug.watch.message.HandlerInvocationBridge;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.NamedBlock;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;
import java.util.concurrent.Callable;

public class FunctionExecutionTask implements Callable<Value> {

    private final ExecutionContext context;
    private final NamedBlock function;
    private final Expression arguments;
    private final PartSpecifier me;

    public FunctionExecutionTask(ExecutionContext context, PartSpecifier me, NamedBlock function, Expression arguments) {
        this.context = context;
        this.function = function;
        this.arguments = arguments;
        this.me = me;
    }

    @Override
    public Value call() throws HtException {

        HandlerInvocationBridge.getInstance().notifyMessageHandled(Thread.currentThread(), function.name, me, context.getStackDepth(), true);

        // Arguments passed to function must be evaluated in the context of the caller (i.e., before we push a new stack frame)
        List<Value> evaluatedArguments = arguments.evaluateAsList(context);

        context.pushStackFrame();
        context.pushMe(me);
        context.setParams(evaluatedArguments);
        context.setMessage(function.name);

        try {
            // Bind argument values to parameter variables in this context
            for (int index = 0; index < function.parameters.list.size(); index++) {

                // Missing arguments are populated with empty
                String theParam = function.parameters.list.get(index);
                Value theArg = evaluatedArguments.size() > index ? evaluatedArguments.get(index) : new Value();

                context.setVariable(theParam, theArg);
            }

            try {
                function.statements.execute(context);
            } catch (Breakpoint breakpoint) {
                // Nothing to do
            }
        
        } catch (HtSemanticException e) {
            WyldCard.getInstance().showErrorDialog(e);
        }

        Value returnValue = context.getReturnValue();

        context.popStackFrame();
        context.popMe();

        return returnValue;
    }
}
