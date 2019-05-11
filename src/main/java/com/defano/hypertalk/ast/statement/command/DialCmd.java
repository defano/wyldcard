package com.defano.hypertalk.ast.statement.command;

import com.defano.wyldcard.runtime.ExecutionContext;
import com.defano.wyldcard.sound.SoundManager;
import com.defano.wyldcard.sound.SoundSample;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.statement.Command;
import com.defano.hypertalk.exception.HtException;
import com.google.inject.Inject;
import org.antlr.v4.runtime.ParserRuleContext;

public class DialCmd extends Command {

    @Inject
    private SoundManager soundManager;

    private final Expression expression;

    public DialCmd(ParserRuleContext context, Expression expression) {
        super(context, "dial");
        this.expression = expression;
    }

    @Override
    public void onExecute(ExecutionContext context) throws HtException {
        for (char thisChar : expression.evaluate(context).toString().toCharArray()) {
            if ((thisChar >= '0' && thisChar <= '9') || thisChar == '*' || thisChar == '#') {
                SoundSample sample = SoundSample.ofTouchTone(thisChar);
                soundManager.play(sample);
            }
        }
    }

}
