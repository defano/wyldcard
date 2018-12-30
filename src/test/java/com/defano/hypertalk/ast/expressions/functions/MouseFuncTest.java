package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MouseFuncTest extends GuiceTest<MouseFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new MouseFunc(mockParserRuleContext));
    }

    @Test
    public void testMouseIsDown() {
        // Setup
        final Value expectedResult = new Value("down");

        Mockito.when(mockMouseManager.isMouseDown()).thenReturn(true);

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testMouseIsUp() {
        // Setup
        final Value expectedResult = new Value("up");

        Mockito.when(mockMouseManager.isMouseDown()).thenReturn(false);

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
