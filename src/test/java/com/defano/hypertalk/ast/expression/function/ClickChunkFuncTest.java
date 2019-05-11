package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ClickChunkFuncTest extends GuiceTest<ClickChunkFunc> {

    @Mock
    private Value expectedChunk;

    @BeforeEach
    public void setup() {
        initialize(new ClickChunkFunc(mockParserRuleContext));
    }

    @Test
    public void testThatClickChunkReturnsChunk() throws HtException {
        Mockito.when(mockSelectionManager.getClickChunk()).thenReturn(expectedChunk);
        assertEquals(uut.onEvaluate(mockExecutionContext), expectedChunk);
    }
}