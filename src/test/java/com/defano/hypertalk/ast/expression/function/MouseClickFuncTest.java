package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MouseClickFuncTest extends GuiceTest<MouseClickFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new MouseClickFunc(mockParserRuleContext));
    }

    @Test
    public void testClickAfterFrameCreation() throws Exception {
        // Setup
        final Value expectedResult = new Value(true);

        Mockito.when(mockExecutionContext.getStackFrame().getCreationTimeMs()).thenReturn(0L);
        Mockito.when(mockMouseManager.getClickTimeMs()).thenReturn(1L);

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testClickBeforeFrameCreation() throws Exception {
        // Setup
        final Value expectedResult = new Value(false);

        Mockito.when(mockExecutionContext.getStackFrame().getCreationTimeMs()).thenReturn(1L);
        Mockito.when(mockMouseManager.getClickTimeMs()).thenReturn(0L);

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
