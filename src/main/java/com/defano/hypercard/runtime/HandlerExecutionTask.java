/*
 * HandlerExecutionTask
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:10 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypercard.runtime;

import com.defano.hypercard.HyperCard;
import com.defano.hypercard.context.GlobalContext;
import com.defano.hypertalk.ast.containers.PartSpecifier;
import com.defano.hypertalk.ast.statements.StatementList;
import com.defano.hypertalk.exception.HtException;

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
            HyperCard.getInstance().showErrorDialog(e);
        }
    }

}
