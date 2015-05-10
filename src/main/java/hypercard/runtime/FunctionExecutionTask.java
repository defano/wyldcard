package hypercard.runtime;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.ast.functions.UserFunction;
import hypertalk.exception.HtSyntaxException;

import java.util.concurrent.Callable;

public class FunctionExecutionTask implements Callable<Value> {

	private UserFunction function;
	private ArgumentList arguments;

	public FunctionExecutionTask (UserFunction function, ArgumentList arguments) {
		this.function = function;
		this.arguments = arguments;
		
		if (function.parameters.list.size() != arguments.getEvaluatedList().size())
			RuntimeEnv.getRuntimeEnv().dialogSyntaxError(new HtSyntaxException("Argument count to function " + function.name + " doesn't match parameter count"));		
	}

	
	@Override
	public Value call() throws Exception {
		GlobalContext.getContext().pushContext();
		
		try {		
			for (int index = 0; index < function.parameters.list.size(); index++) {
			
				String theParam = function.parameters.list.get(index);
				Value theArg = arguments.getEvaluatedList().get(index);
				
				GlobalContext.getContext().set(theParam, theArg);					
			}
			
			function.statements.execute();
		
		} catch (HtSyntaxException e) {
			RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
		}

		Value returnValue = GlobalContext.getContext().getReturnValue();
		GlobalContext.getContext().popContext();	
		
		return returnValue;
	}
}
