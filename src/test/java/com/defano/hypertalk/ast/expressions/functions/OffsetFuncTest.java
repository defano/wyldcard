package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OffsetFuncTest extends GuiceTest<OffsetFunc> {

    private Expression offsetArgs = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {
        initialize(new OffsetFunc(mockParserRuleContext, offsetArgs));
    }

    @Test
    public void testSubstringOffset() throws Exception {
        // Setup
        final Value expectedResult = new Value(3);

        Mockito.when(offsetArgs.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value("cde"), new Value("abcdefghi")
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testDistinctStringsOffset() throws Exception {
        // Setup
        final Value expectedResult = new Value(0);

        Mockito.when(offsetArgs.evaluateAsList(mockExecutionContext)).thenReturn(Lists.newArrayList(
                new Value("xyz"), new Value("abcdefghi")
        ));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

}
