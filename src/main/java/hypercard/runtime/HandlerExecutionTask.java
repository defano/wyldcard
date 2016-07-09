package hypercard.runtime;

import hypercard.context.GlobalContext;
import hypertalk.ast.statements.StatementList;
import hypertalk.exception.HtException;

public class HandlerExecutionTask implements Runnable {

	private final StatementList handler;
	
	public HandlerExecutionTask (StatementList handler) {
		this.handler = handler;
	}
	
	@Override
	public void run() {
		try {
            GlobalContext.getContext().newLocalContext();
            GlobalContext.getContext().setNoMessages(true);
			handler.execute();			
		} catch (HtException e) {
			RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
		} finally {
			GlobalContext.getContext().setNoMessages(false);
		}			
	}

}
