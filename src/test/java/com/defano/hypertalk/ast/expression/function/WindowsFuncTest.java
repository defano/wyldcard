package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtException;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WindowsFuncTest extends GuiceTest<WindowsFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new WindowsFunc(mockParserRuleContext));
    }

    @Test
    public void testWindowsFunc() throws HtException {
        // Setup
        final Value expectedResult = new Value("Window 1\nWindow 2");
        Mockito.when(mockWindowManager.getWindowNames()).thenReturn(Lists.newArrayList(new Value("Window 1"), new Value("Window 2")));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
