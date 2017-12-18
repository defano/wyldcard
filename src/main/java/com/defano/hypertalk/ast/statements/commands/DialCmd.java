package com.defano.hypertalk.ast.statements.commands;

import com.defano.hypercard.sound.SoundPlayer;
import com.defano.hypercard.sound.SoundSample;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.statements.Command;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class DialCmd extends Command {

    private final Expression expression;

    public DialCmd(ParserRuleContext context, Expression expression) {
        super(context, "dial");
        this.expression = expression;
    }

    @Override
    public void onExecute() throws HtException {
        for (char thisChar : expression.evaluate().stringValue().toCharArray()) {
            if ((thisChar >= '0' && thisChar <= '9') || thisChar == '*' || thisChar == '#') {
                SoundSample sample = SoundSample.ofTouchTone(thisChar);
                SoundPlayer.play(sample);
            }
        }
    }

}
