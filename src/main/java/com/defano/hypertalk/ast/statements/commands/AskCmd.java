package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.expressions.LiteralExp;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class AskCmd extends Command {

    public final Expression question;
    public final Expression suggestion;
    
    public AskCmd(ParserRuleContext context, Expression question, Expression suggestion) {
        super(context, "ask");

        this.question = question;
        this.suggestion = suggestion;
    }
    
    public AskCmd(ParserRuleContext context,  Expression question) {
        super(context, "ask");

        this.question = question;
        this.suggestion = new LiteralExp(context, "");
    }
    
    public void onExecute () throws HtException {
        if (suggestion != null)
            ask(question.evaluate(), suggestion.evaluate());
        else
            ask(question.evaluate());
    }
    
    private void ask (Value question, Value suggestion) {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>();
        
        SwingUtilities.invokeLater(() -> {
            Component parent = WindowManager.getInstance().getStackWindow().getWindowPanel();

            result.set((String) JOptionPane.showInputDialog(
                    parent,
                    question,
                    "Ask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    suggestion));

            if (result.get() == null)
                result.set("");

            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        ExecutionContext.getContext().setIt(new Value(result.get()));
    }
    
    private void ask (Value question) {
        ask(question, new Value());
    }
}
