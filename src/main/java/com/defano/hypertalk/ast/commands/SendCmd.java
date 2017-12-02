package com.defano.hypertalk.ast.commands;

import com.defano.hypercard.runtime.CompilationUnit;
import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.runtime.Interpreter;
import com.defano.hypertalk.ast.common.Script;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.PartExp;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

public class SendCmd extends Command {

    public final PartExp part;
    public final Expression message;

    public SendCmd(ParserRuleContext context, PartExp part, Expression message) {
        super(context, "send");

        this.part = part;
        this.message = message;
    }

    public void onExecute() throws HtException {
        ExecutionContext.getContext().pushMe(part.evaluateAsSpecifier());

        MessageCmd messageCmd = interpretMessage(message.evaluate().stringValue());
        if (messageCmd == null) {
            ExecutionContext.getContext().sendMessage(part.evaluateAsSpecifier(), message.evaluate().stringValue(), new ArrayList<>());
        } else {
            messageCmd.onExecute();
        }

        ExecutionContext.getContext().popMe();
    }

    private MessageCmd interpretMessage(String message) {
        try {
            Script compiled = Interpreter.compile(CompilationUnit.SCRIPTLET, message);
            return (MessageCmd) compiled.getStatements().list.get(0);
        } catch (Exception e) {
            return null;
        }
    }

}
