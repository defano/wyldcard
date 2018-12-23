package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MouseLocFuncTest extends GuiceTest<MouseLocFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new MouseLocFunc(mockParserRuleContext));
    }

    @Test
    public void testMouseLoc() {
        // Setup
        final Value expectedResult = new Value("1,2");
        Mockito.when(mockMouseManager.getMouseLoc(mockExecutionContext)).thenReturn(new Point(1, 2));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
