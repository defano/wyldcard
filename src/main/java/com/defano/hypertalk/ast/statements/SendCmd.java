/*
 * StatSendCmd
 * hypertalk-java
 *
 * Created by Matt DeFano on 2/19/17 3:11 PM.
 * Copyright © 2017 Matt DeFano. All rights reserved.
 */

package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Script;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.exception.HtException;

import java.util.ArrayList;

public class SendCmd extends Command {

    public final PartExp part;
    public final Expression message;

    public SendCmd(PartExp part, Expression message) {
        super("send");

        this.part = part;
        this.message = message;
    }

    public void onExecute() throws HtException {
        ExecutionContext.getContext().setMe(part.evaluateAsSpecifier());

        MessageCmd messageCmd = interpretMessage(message.evaluate().stringValue());
        if (messageCmd == null) {
            ExecutionContext.getContext().sendMessage(part.evaluateAsSpecifier(), message.evaluate().stringValue(), new ArrayList<>());
        } else {
            messageCmd.execute();
        }
    }

    private MessageCmd interpretMessage(String message) {
        try {
            Script compiled = Interpreter.compile(message);
            return (MessageCmd) compiled.getStatements().list.get(0);
        } catch (Exception e) {
            return null;
        }
    }

}
