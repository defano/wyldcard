package com.defano.hypertalk.ast.statements.commands;

import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.defano.wyldcard.sound.DefaultSoundManager;
import com.defano.wyldcard.sound.SoundManager;
import com.defano.wyldcard.sound.SoundSample;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
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
        for (char thisChar : expression.evaluate(context).stringValue().toCharArray()) {
            if ((thisChar >= '0' && thisChar <= '9') || thisChar == '*' || thisChar == '#') {
                SoundSample sample = SoundSample.ofTouchTone(thisChar);
                soundManager.play(sample);
            }
        }
    }

}
