/**
 * HandlerExecutionThread.java
 * @author matt.defano@gmail.com
 * 
 * Thread class for running a script handler within. A separate thread is 
 * required to allow the handler to interact with the Swing UI. If the  
 * script runs in the same thread as Swing, the UI will appear to lockup.
 * 
 * The constructor returns immediately thus making it possible to execute
 * multiple handlers in parallel.
 */

package hypercard.runtime;

import hypercard.context.GlobalContext;
import hypertalk.ast.statements.StatementList;
import hypertalk.exception.HtSyntaxException;

import java.io.Serializable;

public class HandlerExecutionThread implements Runnable, Serializable {
private static final long serialVersionUID = -3099317228266781130L;

	private StatementList handler;
	
	public HandlerExecutionThread (StatementList handler) {
		this.handler = handler;
		
		Thread execution = new Thread(this);
		execution.start();
	}
	
	public void run () {
		try {
            GlobalContext.getContext().newLocalContext();
            GlobalContext.getContext().setNoMessages(true);
			handler.execute();			
		} catch (HtSyntaxException e) {
			RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
		} finally {
			GlobalContext.getContext().setNoMessages(false);
		}			
	}	
}
