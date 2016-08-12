package hypercard.runtime;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.ast.functions.UserFunction;
import hypertalk.exception.HtSemanticException;

import java.util.concurrent.Callable;

public class FunctionExecutionTask implements Callable<Value> {

    private final UserFunction function;
    private final ArgumentList arguments;
    private final PartSpecifier me;

    public FunctionExecutionTask (PartSpecifier me, UserFunction function, ArgumentList arguments) {
        this.function = function;
        this.arguments = arguments;
        this.me = me;

        if (function.parameters.list.size() != arguments.getArgumentCount())
            RuntimeEnv.getRuntimeEnv().dialogSyntaxError(new HtSemanticException("Argument count to function " + function.name + " doesn't match parameter count"));
    }

    
    @Override
    public Value call() throws Exception {
        GlobalContext.getContext().pushContext();
        GlobalContext.getContext().setMe(me);
        
        try {        
            for (int index = 0; index < function.parameters.list.size(); index++) {
            
                String theParam = function.parameters.list.get(index);
                Value theArg = arguments.getEvaluatedList().get(index);
                
                GlobalContext.getContext().set(theParam, theArg);                    
            }
            
            function.statements.execute();
        
        } catch (HtSemanticException e) {
            RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
        }

        Value returnValue = GlobalContext.getContext().getReturnValue();
        GlobalContext.getContext().popContext();    
        
        return returnValue;
    }
}
