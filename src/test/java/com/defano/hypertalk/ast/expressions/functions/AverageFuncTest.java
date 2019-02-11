package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AverageFuncTest extends GuiceTest<AverageFunc> {

    private Expression mockExpression = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {
        initialize(new AverageFunc(mockParserRuleContext, mockExpression));
    }

    @Test
    public void testInvalidArguments() throws Exception {
        // Setup
        Mockito.when(mockExpression.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value("barf"), new Value(3), new Value(5), new Value(7)
        ));

        // Run the test
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNoArguments() throws Exception {
        // Setup
        final Value expectedResult = new Value(0);

        Mockito.when(mockExpression.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList());

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testAverageCalculation() throws Exception {
        // Setup
        final Value expectedResult = new Value(4.0);

        Mockito.when(mockExpression.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value(1), new Value(3), new Value(5), new Value(7)
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }
}
