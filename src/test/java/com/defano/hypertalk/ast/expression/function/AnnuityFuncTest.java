package com.defano.hypertalk.ast.expression.function;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expression.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnnuityFuncTest extends GuiceTest<AnnuityFunc> {

    private Expression mockExpressionList = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {
        initialize(new AnnuityFunc(mockParserRuleContext, mockExpressionList));
    }

    @Test
    public void testWithRateAndPeriod() throws Exception {
        final Value expectedResult = new Value(0.1);

        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext))
                .thenReturn(Lists.newArrayList(new Value(10), new Value(20)));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testWithIllegalRate() throws Exception {
        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext))
                .thenReturn(Lists.newArrayList(new Value("barf"), new Value("10")));

        // Run the test
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testWithIllegalPeriod() throws Exception {
        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext))
                .thenReturn(Lists.newArrayList(new Value("10"), new Value("barf")));

        // Run the test
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

}
