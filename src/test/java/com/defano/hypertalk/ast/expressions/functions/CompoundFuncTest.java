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

public class CompoundFuncTest extends GuiceTest<CompoundFunc> {

    private Expression mockExpressionList = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {
        initialize(new CompoundFunc(mockParserRuleContext, mockExpressionList));
    }

    @Test
    public void testCompound() throws Exception {
        // Setup
        final Value expectedResult = new Value(34.32);
        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
            new Value(2.25), new Value(3)
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult.doubleValue(), result.doubleValue(), .01);
    }

    @Test
    public void testBogusRate() throws Exception {
        // Setup
        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value("barf"), new Value(3)
        ));

        // Run the test
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testBogusPeriod() throws Exception {
        // Setup
        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value(22), new Value("barf")
        ));

        // Run the test
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

    @Test
    public void testBogusArgumentCount() throws Exception {
        // Setup
        Mockito.when(mockExpressionList.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value(22)
        ));

        // Run the test
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

}
