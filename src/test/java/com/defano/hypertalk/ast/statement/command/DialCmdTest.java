package com.defano.hypertalk.ast.statement.command;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.defano.wyldcard.sound.SoundSample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DialCmdTest extends GuiceTest<DialCmd> {

    private Expression mockDialExpr = Mockito.mock(Expression.class);

    @BeforeEach
    void setUp() {
        initialize(new DialCmd(mockParserRuleContext, mockDialExpr));
    }

    @Test
    void testThatNumbersAreDialed() throws HtException {
        // Setup
        Mockito.when(mockDialExpr.evaluate(mockExecutionContext)).thenReturn(new Value("123-4567"));

        // Execute
        uut.onExecute(mockExecutionContext);

        // Verify
        Mockito.verify(mockSoundManager).play(SoundSample.DIAL_1);
        Mockito.verify(mockSoundManager).play(SoundSample.DIAL_2);
        Mockito.verify(mockSoundManager).play(SoundSample.DIAL_3);
        Mockito.verify(mockSoundManager).play(SoundSample.DIAL_4);
        Mockito.verify(mockSoundManager).play(SoundSample.DIAL_5);
        Mockito.verify(mockSoundManager).play(SoundSample.DIAL_6);
        Mockito.verify(mockSoundManager).play(SoundSample.DIAL_7);

        Mockito.verifyNoMoreInteractions(mockSoundManager);
    }
}