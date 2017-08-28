package com.defano.hypertalk.ast.statements;

import com.defano.hypercard.gui.sound.SoundPlayer;
import com.defano.hypercard.gui.sound.SoundSample;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.exception.HtException;

public class DialCmd extends Statement {

    private final Expression expression;

    public DialCmd(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute() throws HtException {
        for (char thisChar : expression.evaluate().stringValue().toCharArray()) {
            if ((thisChar >= '0' && thisChar <= '9') || thisChar == '*' || thisChar == '#') {
                SoundSample sample = SoundSample.fromName("dial" + thisChar);
                SoundPlayer.play(sample);
            }
        }
    }

}
