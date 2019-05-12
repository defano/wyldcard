package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.model.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResultFuncTest extends GuiceTest<ResultFunc> {

    @BeforeEach
    public void setUp() {
        initialize(new ResultFunc(mockParserRuleContext));
    }

    @Test
    public void testOnEvaluate() {
        // Setup
        final Value expectedResult = new Value("The result");
        Mockito.when(mockExecutionContext.getResult()).thenReturn(expectedResult);

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
