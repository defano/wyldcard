package com.defano.hypercard.runtime;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypertalk.ast.breakpoints.Breakpoint;
import com.defano.hypertalk.ast.common.NamedBlock;
import com.defano.hypertalk.ast.common.Value;
import com.defano.hypertalk.ast.specifiers.PartSpecifier;
import com.defano.hypertalk.ast.common.ExpressionList;
import com.defano.hypertalk.exception.HtException;
import com.defano.hypertalk.exception.HtSemanticException;

import java.util.List;
import java.util.concurrent.Callable;

public class FunctionExecutionTask implements Callable<Value> {

    private final NamedBlock function;
    private final ExpressionList arguments;
    private final PartSpecifier me;

    public FunctionExecutionTask (PartSpecifier me, NamedBlock function, ExpressionList arguments) {
        this.function = function;
        this.arguments = arguments;
        this.me = me;
    }

    @Override
    public Value call() throws HtException {

        // Arguments passed to function must be evaluated in the context of the caller (i.e., before we push a new stack frame)
        List<Value> evaluatedArguments = arguments.evaluateDisallowingCoordinates();

        ExecutionContext.getContext().pushContext();
        ExecutionContext.getContext().setMe(me);
        ExecutionContext.getContext().setParams(evaluatedArguments);
        ExecutionContext.getContext().setMessage(function.name);

        if (function.parameters.list.size() != evaluatedArguments.size()) {
            throw new HtSemanticException("Function '" + function.name + "' expects " + function.parameters.list.size() + " arguments, but got " + evaluatedArguments.size() + ".");
        }

        try {
            // Bind argument values to parameter variables in this context
            for (int index = 0; index < function.parameters.list.size(); index++) {
                String theParam = function.parameters.list.get(index);
                Value theArg = evaluatedArguments.get(index);

                ExecutionContext.getContext().setVariable(theParam, theArg);
            }

            try {
                function.statements.execute();
            } catch (Breakpoint breakpoint) {
                // Nothing to do
            }
        
        } catch (HtSemanticException e) {
            HyperCard.getInstance().showErrorDialog(e);
        }

        Value returnValue = ExecutionContext.getContext().getReturnValue();
        ExecutionContext.getContext().popContext();
        
        return returnValue;
    }
}
