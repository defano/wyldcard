package hypercard.runtime;

import hypercard.context.GlobalContext;
import hypertalk.ast.containers.PartSpecifier;
import hypertalk.ast.statements.StatementList;
import hypertalk.exception.HtException;

public class HandlerExecutionTask implements Runnable {

    private final StatementList handler;
    private final PartSpecifier me;
    
    public HandlerExecutionTask (PartSpecifier me, StatementList handler) {
        this.handler = handler;
        this.me = me;
    }
    
    @Override
    public void run() {
        try {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

            GlobalContext.getContext().newLocalContext();
            GlobalContext.getContext().setMe(me);

            handler.execute();
        } catch (HtException e) {
            RuntimeEnv.getRuntimeEnv().dialogSyntaxError(e);
        }
    }

}
