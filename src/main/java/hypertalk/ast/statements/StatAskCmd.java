/**
 * StatAskCmd.java
 * @author matt.defano@gmail.com
 * 
 * Implementation of the "ask" statement
 */

package hypertalk.ast.statements;

import hypercard.context.GlobalContext;
import hypercard.runtime.RuntimeEnv;
import hypercard.runtime.WindowManager;
import hypertalk.ast.common.Value;
import hypertalk.ast.expressions.ExpLiteral;
import hypertalk.ast.expressions.Expression;
import hypertalk.exception.HtSemanticException;

import java.awt.*;
import java.util.concurrent.CountDownLatch;

import javax.swing.*;

public class StatAskCmd extends Statement {

    public final Expression question;
    public final Expression suggestion;
    
    public StatAskCmd (Expression question, Expression suggestion) {
        this.question = question;
        this.suggestion = suggestion;
    }
    
    public StatAskCmd (Expression question) {
        this.question = question;
        this.suggestion = new ExpLiteral("");
    }
    
    public void execute () throws HtSemanticException {
        if (suggestion != null)
            ask(question.evaluate(), suggestion.evaluate());
        else
            ask(question.evaluate());
    }
    
    private void ask (Value question, Value suggestion) {

        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            Component parent = WindowManager.getStackWindow().getWindowPanel();

            String result = (String)JOptionPane.showInputDialog(
                    parent,
                    question,
                    "Ask",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    suggestion);

            if (result == null)
                result = "";

            GlobalContext.getContext().setIt(new Value(result));
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }
    
    private void ask (Value question) {
        ask(question, new Value());
    }
}
