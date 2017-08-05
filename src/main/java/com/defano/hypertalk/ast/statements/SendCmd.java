/*
 * StatSendCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

/**
 * SendCmd.java
 * @author matt.defano@gmail.com
 * 
 * Encapsulation of the "send" command (for passing an event message to a part)
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.context.ExecutionContext;
import com.defano.hypercard.HyperCard;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.PartExp;

public class SendCmd extends Statement {

    public final PartExp part;
    public final Expression message;
    
    public SendCmd(PartExp part, Expression message) {
        this.part = part;
        this.message = message;
    }
    
    public void execute () {
        try {
            ExecutionContext.getContext().sendMessage(part.evaluateAsSpecifier(), message.evaluate().stringValue());
        } catch (Exception e) {
            HyperCard.getInstance().showErrorDialog(e);
        }
    }
}
