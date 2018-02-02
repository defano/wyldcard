package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.runtime.context.ExecutionContext;
import com.defano.hypercard.window.WindowManager;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class AnswerCmd extends Command {

    public final Expression message;
    public final Expression ch1;
    public final Expression ch2;
    public final Expression ch3;
    
    public AnswerCmd(ParserRuleContext context, Expression message, Expression ch1, Expression ch2, Expression ch3) {
        super(context, "answer");

        this.message = message;
        this.ch1 = ch1;
        this.ch2 = ch2;
        this.ch3 = ch3;
    }
    
    public AnswerCmd(ParserRuleContext context, Expression message, Expression ch1, Expression ch2) {
        this(context, message, ch1, ch2, null);
    }
    
    public AnswerCmd(ParserRuleContext context, Expression message, Expression ch1) {
        this(context, message, ch1, null, null);
    }

    public AnswerCmd(ParserRuleContext context, Expression message) {
        this(context, message, null, null, null);
    }
    
    public void onExecute () throws HtException {
        if (ch1 != null && ch2 != null && ch3 != null)
            answer(message.evaluate(), ch1.evaluate(), ch2.evaluate(), ch3.evaluate());
        else if (ch1 != null && ch2 != null)
            answer(message.evaluate(), ch1.evaluate(), ch2.evaluate());
        else if (ch1 != null)
            answer(message.evaluate(), ch1.evaluate());
        else
            answer(message.evaluate());
    }
    
    private void answer (Value msg, Value choice1, Value choice2, Value choice3) {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger choice = new AtomicInteger();

        SwingUtilities.invokeLater(() -> {
            Component parent = WindowManager.getInstance().getStackWindow().getWindowPanel();
            Object[] choices = null;

            if (choice1 != null && choice2 != null && choice3 != null) {
                choices = new Object[]{choice1, choice2, choice3};
            }
            else if (choice1 != null && choice2 != null) {
                choices = new Object[]{choice1, choice2};
            }
            else if (choice1 != null) {
                choices = new Object[]{choice1};
            }

            choice.set(JOptionPane.showOptionDialog(parent, msg, "Answer",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]));

            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        switch (choice.get()) {
            case 0:     ExecutionContext.getContext().setIt(choice1); break;
            case 1:     ExecutionContext.getContext().setIt(choice2); break;
            case 2:     ExecutionContext.getContext().setIt(choice3); break;
            default:    ExecutionContext.getContext().setIt(new Value()); break;
        }
    }

    private void answer (Value msg, Value choice1, Value choice2) {
        answer(msg, choice1, choice2, null);
    }
    
    private void answer (Value msg, Value choice1) {
        answer(msg, choice1, null, null);
    }
    
    private void answer (Value msg) {
        answer(msg, new Value("OK"), null, null);
    }
}
