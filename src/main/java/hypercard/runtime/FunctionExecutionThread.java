/**
 * FunctionExecutionThread.java
 * @author matt.defano@gmail.com
 * 
 * Thread class for running user defined function within. A separate thread is 
 * required to allow the function to interact with the Swing UI. If the  
 * script runs in the same thread as Swing, the UI will appear to lockup.
 * 
 * Note that the constructor of this class does not return until the thread
 * has finished execution. User functions should be able to run in parallel.
 */

package hypercard.runtime;

import hypercard.context.GlobalContext;
import hypertalk.ast.common.Value;
import hypertalk.ast.functions.ArgumentList;
import hypertalk.ast.functions.UserFunction;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class FunctionExecutionThread implements Runnable, Serializable {
private static final long serialVersionUID = -2025287567616987117L;

	private UserFunction function;
	private ArgumentList arguments;
	private Value returnValue;
	
	public FunctionExecutionThread (UserFunction function, ArgumentList arguments) {
		this.function = function;
		this.arguments = arguments;
		
		if (function.parameters.list.size() != arguments.getEvaluatedList().size())
			RuntimeEnv.getRuntimeEnv().dialogSyntaxError(new HtSyntaxException("Argument count to function " + function.name + " doesn't match parameter count"));
		
		Thread execution = new Thread(this);
		execution.start();
		
		// Wait for the handler to finish executing; functions should execute atomically
		try {
			execution.join();
		} catch (InterruptedException e) {}
	}
	
	public Value getReturnValue () {
		return returnValue;
	}
	
	public void run () {
		
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

		returnValue = GlobalContext.getContext().getReturnValue();
		GlobalContext.getContext().popContext();				
	}
}
