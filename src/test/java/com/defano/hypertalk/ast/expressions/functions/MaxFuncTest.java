package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import com.google.common.collect.Lists;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

public class MaxFuncTest extends GuiceTest<MaxFunc> {

    private Expression mockArgListExpression = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {
        initialize(new MaxFunc(mockParserRuleContext, mockArgListExpression));
    }

    @Test
    public void testMaxFunc() throws Exception {
        // Setup
        final Value expectedResult = new Value(30);
        Mockito.when(mockArgListExpression.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value(10), new Value(30), new Value(20)
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testBogusArgs() throws Exception {
        // Setup
        Mockito.when(mockArgListExpression.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value(10), new Value(30), new Value("barf")
        ));

        // Verify the results
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testNoArgs() throws Exception {
        // Setup
        final Value expectedResult = new Value(0);
        Mockito.when(mockArgListExpression.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList());

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), 0.001);
    }

}
