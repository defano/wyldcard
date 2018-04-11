package com.defano.wyldcard.runtime.interpreter;

import com.defano.hypertalk.ast.preemptions.Preemption;
import com.defano.hypertalk.ast.expressions.Expression;
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

        // Arguments passed to function must be evaluated in the context of the caller (i.e., before we push a new stack frame)
        List<Value> evaluatedArguments = arguments.evaluateAsList(context);

        HandlerInvocationBridge.getInstance().notifyMessageHandled(new HandlerInvocation(Thread.currentThread().getName(), function.name, evaluatedArguments, me, context.getStackDepth(), true));

        context.pushStackFrame(function.name, me, evaluatedArguments);

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
            } catch (Preemption preemption) {
                // Nothing to do
            }
        
        } catch (HtSemanticException e) {
            WyldCard.getInstance().showErrorDialog(e);
        }

        Value returnValue = context.getStackFrame().getReturnValue();

        context.popStackFrame();

        return returnValue;
    }
}
