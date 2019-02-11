package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpeechFuncTest extends GuiceTest<SpeechFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new SpeechFunc(mockParserRuleContext));
    }

    @Test
    public void testTheSpeech() throws HtException {
        Mockito.when(mockSpeechPlaybackManager.getTheSpeech()).thenReturn(new Value("The speech!"));
        assertEquals(new Value("The speech!"), uut.evaluate(mockExecutionContext));
    }
}
