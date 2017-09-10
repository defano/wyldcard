package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.sound.SoundPlayer;
import com.defano.hypercard.gui.sound.SoundSample;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class DialCmd extends Command {

    private final Expression expression;

    public DialCmd(Expression expression) {
        super("dial");
        this.expression = expression;
    }

    @Override
    public void onExecute() throws HtException {
        for (char thisChar : expression.evaluate().stringValue().toCharArray()) {
            if ((thisChar >= '0' && thisChar <= '9') || thisChar == '*' || thisChar == '#') {
                SoundSample sample = SoundSample.fromName("dial" + thisChar);
                SoundPlayer.play(sample);
            }
        }
    }

}
