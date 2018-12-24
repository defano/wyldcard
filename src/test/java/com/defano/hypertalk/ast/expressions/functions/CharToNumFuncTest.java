package com.defano.hypertalk.ast.expressions.functions;

import com.defano.hypertalk.GuiceTest;
import com.defano.hypertalk.ast.expressions.Expression;
import com.defano.hypertalk.ast.model.Value;
import com.defano.hypertalk.exception.HtSemanticException;
import com.defano.wyldcard.runtime.context.ExecutionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CharToNumFuncTest extends GuiceTest<CharToNumFunc> {

    private Expression mockExpression = Mockito.mock(Expression.class);

    @BeforeEach
    public void setUp() {
        initialize(new CharToNumFunc(mockParserRuleContext, mockExpression));
    }

    @Test
    public void testCharConversion() throws Exception {
        // Setup
        final Value expectedResult = new Value(65);
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value("A"));

        // Run the test
        final Value result = uut.onEvaluate(mockExecutionContext);

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testEmptyStringConversion() throws Exception {
        // Setup
        Mockito.when(mockExpression.evaluate(mockExecutionContext)).thenReturn(new Value(""));

        // Run the test
        assertThrows(HtSemanticException.class, () -> uut.onEvaluate(mockExecutionContext));
    }

}
